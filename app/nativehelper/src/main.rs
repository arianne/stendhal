use clap::Parser;

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

fn authenticate() {
    match Client::init() {
        Ok((client, single)) => {
            let (ticket_handle, session_ticket): (steamworks::AuthTicket, Vec<u8>) = client.user().authentication_session_ticket();
            let _callback =
            client.register_callback(move |session_ticket_response: AuthSessionTicketResponse| {
                //if session_ticket_response.ticket == ticket_handle {
                println!("Ticket Response Result: {:?}", session_ticket_response.result);
                // println!("Ticket : {:?}", session_ticket_response.ticket);
                println!("Ticket Response: {:?}", session_ticket);
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
    println!("{}", args.nl_port);
    authenticate();
}
