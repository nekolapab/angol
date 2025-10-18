#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting Flutter Web Server...${NC}"

# Clean previous build
echo -e "${BLUE}Cleaning previous build...${NC}"
flutter clean
flutter pub get
echo -e "${BLUE}Deleting old files...${NC}"
find /home/user/myapp/.dart_tool -type f -mtime +7 -delete

# Kill any existing Flutter processes
echo -e "${BLUE}Killing existing Flutter processes...${NC}"
pkill -f "flutter run"
sleep 2

# Start Flutter web server
echo -e "${GREEN}Starting web server on port 12345...${NC}"
echo -e "${GREEN}Access your app at:${NC}"
echo -e "${BLUE}https://12345-firebase-angol-1760150648200.cluster-c72u3gwiofapkvxrcwjq5zllcu.cloudworkstations.dev/${NC}"
echo ""

# Run with verbose output so you can see what's happening
flutter run -d web-server --web-hostname=0.0.0.0 --web-port=12345 --verbose