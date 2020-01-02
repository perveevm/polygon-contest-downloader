# Polygon2Ejudge

## Usage
1. Create your API key in Polygon (it can be done in Settings), then put your Key and Secret in `scripts/build-contest.sh`
2. Execute `scripts/build-contest.sh` with two arguments: contest path where all problems should be moved, and contest ID in Polygon
3. Have fun

## TODO list
1. Interactive problems support
2. Some errors handling
3. Bug fixes :)
4. Auto-generating problem config (aka `serve.cfg` in Ejudge) to simplify routine work, for example test scores
5. Auto-generating groups config (aka `valuer.cfg` in Ejudge)
