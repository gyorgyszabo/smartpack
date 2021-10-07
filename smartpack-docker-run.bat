@ECHO OFF
ECHO --- Batch file log: Creating and starting a Docker Container from the Image ---
docker run --name smartpack_container --rm -p 8080:8080 smartpack