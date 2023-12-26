use clap::Parser;

use std::sync::mpsc::Sender;
use steamworks::AuthSessionTicketResponse;
use steamworks::Client;

#[derive(Parser)]
struct Cli {
    /// The websocket port
    #[arg(long = "nl-port")]
    nl_port: String,
    /// The authentication token to use the native API
    #[arg(long = "nl-token")]
    nl_token: String,
    // The extension id
    #[arg(long = "nl-extension-id")]
    nl_extension_id: String
}


use std::sync::mpsc::channel;
use std::thread;
use std::thread::JoinHandle;

use websocket::client::ClientBuilder;
use websocket::{Message, OwnedMessage};


fn connect(nl_port: String) -> (Sender<OwnedMessage>, JoinHandle<()>) {
    let connection = "ws://127.0.0.1:".to_owned() + &nl_port;
	println!("Connecting to {}", connection);

	let client = ClientBuilder::new(&connection)
		.unwrap()
		// .add_protocol("rust-websocket")
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
					let _ = tx_1.send(OwnedMessage::Close(None));
					return;
				}
				OwnedMessage::Ping(data) => {
					match tx_1.send(OwnedMessage::Pong(data)) {
						// Send a pong in response
						Ok(()) => (),
						Err(e) => {
							println!("Receive Loop: {:?}", e);
							return;
						}
					}
				}
				// Say what we received
				_ => println!("Receive Loop: {:?}", message),
			}
		}
	});

    return (tx, receive_loop);
}

fn authenticate(tx: Sender<OwnedMessage>, nl_token: String) {
    match Client::init() {
        Ok((client, single)) => {
            let (_ticket_handle, session_ticket): (steamworks::AuthTicket, Vec<u8>) = client.user().authentication_session_ticket();
            let _callback =
            client.register_callback(move |session_ticket_response: AuthSessionTicketResponse| {
                //if session_ticket_response.ticket == ticket_handle {
                println!("Ticket Response Result: {:?}", session_ticket_response.result);
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
                }}", &nl_token, &session_ticket));
                match tx.send(message) {
                    Ok(()) => (),
                    Err(e) => {
                        println!("Websocket send error: {:?}", e);
                    }
                }

            });
            for _ in 0..50 {
                single.run_callbacks();
                ::std::thread::sleep(::std::time::Duration::from_millis(100));
            }
        },
        Err(_) => println!("Steam not available"),
    }
}

fn main() {
    let args = Cli::parse();
    let (tx, receive_loop) = connect(args.nl_port);

//    ::std::thread::sleep(::std::time::Duration::from_millis(5000));
    authenticate(tx, args.nl_token);
    
    println!("Waiting for websocket connection to be closed");
	let _ = receive_loop.join();

}
