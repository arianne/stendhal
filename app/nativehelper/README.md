- https://stackoverflow.com/questions/31492799/cross-compile-a-rust-application-from-linux-to-windows

- apt-get install mingw-w64
- rustup target add x86_64-pc-windows-gnu

cargo rustc --release -- -C link-arg=-Wl,-rpath,\$ORIGIN && cargo build --release --target x86_64-pc-windows-gnu

cp target/release/neutralinojs-steamworks ../stendhal-neutralino/extensions/binary/
cp target/x86_64-pc-windows-gnu/release/neutralinojs-steamworks.exe ../stendhal-neutralino/extensions/binary/
