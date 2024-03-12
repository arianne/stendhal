use serde_json::Value;
use std::io;
use std::io::Read;
use std::sync::Arc;
use std::sync::atomic::AtomicBool;
use std::sync::atomic::Ordering;
use std::sync::mpsc::Sender;
use std::sync::mpsc::channel;
use std::thread;
use std::thread::JoinHandle;

use steamworks::AuthSessionTicketResponse;
use steamworks::Client;

use websocket::client::ClientBuilder;
use websocket::{Message, OwnedMessage};


fn connect() -> (Sender<OwnedMessage>, JoinHandle<()>) {
	println!("Read connection parameters");
	let mut json = String::new();
	io::stdin().read_to_string(&mut json).expect("Read from stdin");
	let connection_params: Value = serde_json::from_str(&json).expect("Parse json from stdin");

	let nl_token = connection_params["nlToken"].as_str().expect("nlToken").to_string();
	let nl_port = connection_params["nlPort"].as_str().expect("nlPort").to_string();
	let nl_connect_token = connection_params["nlConnectToken"].as_str().expect("nlConnectToken").to_string();

	let connection = "ws://127.0.0.1:".to_owned() + &nl_port + "?extensionId=nativehelper&connectToken=" + &nl_connect_token;
	println!("Connecting");

	let client = ClientBuilder::new(&connection)
		.unwrap()
		.connect_insecure()
		.unwrap();

	println!("Successfully connected");

	let (mut receiver, mut sender) = client.split().unwrap();
	let (tx, rx) = channel();
	let tx_1 = tx.clone();
	let _send_loop = thread::spawn(move || {
		loop {
			// Send loop
			let message = match rx.recv() {
				Ok(m) => m,
				Err(e) => {
					println!("Send Loop: {:?}", e);
					return;
				}
			};
			match message {
				OwnedMessage::Close(_) => {
					let _ = sender.send_message(&message);
					// If it's a close message, just send it and then return.
					println!("Send Loop Close");
					return;
				}
				_ => (),
			}
			// Send the message
			match sender.send_message(&message) {
				Ok(()) => (),
				Err(e) => {
					println!("Send Loop: {:?}", e);
					let _ = sender.send_message(&Message::close());
					return;
				}
			}
		}
	});

	let receive_loop = thread::spawn(move || {
		for message in receiver.incoming_messages() {
			let message = match message {
				Ok(m) => m,
				Err(e) => {
					println!("Receive Loop: {:?}", e);
					let _ = tx_1.send(OwnedMessage::Close(None));
					return;
				}
			};
			match message {
				OwnedMessage::Close(_) => {
					// Got a close message, so send a close message and return
					println!("Receive Loop Close");
					let _ = tx_1.send(OwnedMessage::Close(None));
					return;
				}
				OwnedMessage::Ping(data) => {
					println!("Receive Loop Ping");
					match tx_1.send(OwnedMessage::Pong(data)) {
						// Send a pong in response
						Ok(()) => (),
						Err(e) => {
							println!("Receive Loop: {:?}", e);
							return;
						}
					}
				}
				OwnedMessage::Binary(data) =>{
					println!("Receive Loop Binary: {:?}", data);
				}

				// Say what we received
				OwnedMessage::Text(data) => {
					println!("Receive Loop Text: {:?}", data);
					let tx_2 = tx_1.clone();
					handle_message(&nl_token, tx_2, data);
				}
				_ => println!("Receive Loop: {:?}", message)
			}
		}
	});
	
    return (tx, receive_loop);
}

fn handle_message(nl_token: &String, tx: Sender<OwnedMessage>, message: String) {
	if message.contains("event\":\"request_authentication\"") {
		authenticate(tx, nl_token.clone());
	}
}

fn authenticate(tx: Sender<OwnedMessage>, nl_token: String) {
    match Client::init() {
        Ok((client, single)) => {
			let called = Arc::new(AtomicBool::new(false));
			let callback_called = called.clone();

            let (_ticket_handle, session_ticket): (steamworks::AuthTicket, Vec<u8>) = client.user().authentication_session_ticket();
            let _callback =
            client.register_callback(move |session_ticket_response: AuthSessionTicketResponse| {
                //if session_ticket_response.ticket == ticket_handle {
                println!("Ticket Response Result: {:?}", session_ticket_response.result);
                callback_called.store(true, Ordering::Relaxed);
                // println!("Ticket : {:?}", session_ticket_response.ticket);
                // println!("Ticket Response: {:?}", session_ticket);

                let message = OwnedMessage::Text(format!("\
                {{\
                    \"id\": \"15fb0ffb-7b18-48d2-89a3-39b1ce4b2645\",\
                    \"method\": \"app.broadcast\",\
                    \"accessToken\": {:?},\
                    \"data\": {{\
                        \"event\": \"steamAuthToken\",\
                        \"data\": {:?}\
                    }}\
                }}", nl_token, &session_ticket));
                match tx.send(message) {
                    Ok(()) => (),
                    Err(e) => {
                        println!("Websocket send error: {:?}", e);
                    }
                }

            });
            for _ in 0..100 {
                single.run_callbacks();
                if called.load(Ordering::Relaxed) {
                    return;
                }
                ::std::thread::sleep(::std::time::Duration::from_millis(50));
            }
        },
        Err(_) => {
			println!("Steam not available");
			let message = OwnedMessage::Text(format!("\
			{{\
				\"id\": \"15fb0ffb-7b18-48d2-89a3-39b1ce4b2645\",\
				\"method\": \"app.broadcast\",\
				\"accessToken\": {:?},\
				\"data\": {{\
					\"event\": \"noAuthToken\"\
				}}\
			}}", &nl_token));
			match tx.send(message) {
				Ok(()) => (),
				Err(e) => {
					println!("Websocket send error: {:?}", e);
				}
			}
		}
    }
}

fn main() {
    let (_tx, receive_loop) = connect();
	let _ = receive_loop.join();
}
