# Update Algorithm

for each source library
	walk through directory
for each destination library
	walk through directory
	delete any without existing source (based on flag?)

walk through directory (recurse)
	if contains metadata file, read it
	else if contains audio file, warn that no metadata exists
	else if contains cover file, warn that no audio files or metadata exist

read metadata file
	should include
		which audio file for a track
		which cue points within audio file correspond to this track
		which cover file for a track
		which tags for a track

.cache.digest.json
	include date/size/hash of all relevant files
.cache.metadata.json
	include cached version of simplified metadata with track ids
