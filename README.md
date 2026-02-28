# DistributedFileSystem

A distributed file storage system written in Java, built around a control server / storage server architecture.

## How It Works

The system is split into two main components:

**Control Server** acts as the brain. It listens for incoming commands from clients, handles all routing decisions, and coordinates with one or more storage servers to store or retrieve files.

**Storage Servers** are where files actually live. Each storage server is a simple node that receives instructions from the control server. You can run multiple storage servers simultaneously, and the control server distributes files across them.

There are no physical folders anywhere in the system. The storage servers just hold raw files. Instead, the system uses **metadata** to track the virtual folder structure — each file has associated metadata that records its path, name, and location, allowing the system to simulate a full directory tree on top of a flat file store.

## Features

- Central control server that handles all client commands and coordinates storage
- Support for multiple storage servers running in parallel
- Virtual folder/file hierarchy through metadata — no real directories, just files and the records that describe where they belong
- Configurable via `config.properties`

## Project Structure

```
DistributedFileSystem/
├── src/               # Java source files
├── lib/               # Dependencies
└── config.properties  # Server configuration (ports, paths, etc.)
```

## Getting Started

### Prerequisites

- Java JDK 11+

### Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/Abhineet1144/DistributedFileSystem.git
   ```

2. Edit `config.properties` to set your control server address, storage server ports, and any other config values.

3. Compile the source:
   ```bash
   javac -cp lib/* -d out src/**/*.java
   ```

4. Start the control server:
   ```bash
   java -cp out:lib/* ControlServer
   ```

5. Start one or more storage servers:
   ```bash
   java -cp out:lib/* StorageServer
   ```

## Contributing

Pull requests are welcome. For major changes, open an issue first.
