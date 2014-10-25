# Hierarchy

- SourceLibrary
	- DirectoryCache
		- AudioFile
		- ArtFile
		- DigestCache
		- MetadataFile
		- MetadataCache
- DestinationLibrary
	- DirectoryCache
		- AudioFile
		- ArtFile
		- DigestCache

# ThreadPools

- LowEffort
	- Fork Join?
- FileWalking
	- Limit to some number of threads?
- ExternalProcesses
	- Balancing dispatcher?
	- Limit to number of processors.

# Walkthrough

- Service.walkSourceLibrary(SourceLibrary)
- Found directory with music files
- Service.parse
