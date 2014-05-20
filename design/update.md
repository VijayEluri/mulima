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

what changes would trigger a reconversion?
	changed tags -> only change tags of file
	changed album art -> only change album art
	changed cue points -> full reconvert
	changed audio file -> reconvert all tracks that use that file

# Conversion Steps

determine file source and cue points
determine tags
determine cover art
determine file path

if requires re-encoding
	if file source encoded losslessly and preferred format is different
		decode file source to wav
	if temp format is wav or temp format is accepted by lib and has cue points
		split file source on cue points
	if temp format is wav
		encode to preferred format

if requires tag change
	change tags

if requires cover art change
	change cover art

# Library Options

Preferred audio format
Accepted audio formats
Embed cover art?
Copy cover art file into dir?
Resize cover art to AxB size?
Root dir
Tag Precedence (first or last)

