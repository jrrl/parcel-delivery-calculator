
# Parcel Delivery Calculator

**Exercise for GCash**

## Installation

**Prequisites:**
- java 11 or greater
- gradle
- Docker Desktop (optional)

### Run in localhost

To run in your local computer, run the following

```bash 
  # root path of the project
  gradle bootRun
```

To build a jar then run, run the following commands

```bash 
  # root path of the project
  gradle build
  java -jar build\libs\parcel-delivery-calculator-0.0.1.jar
```

To build docker image, run the following:

```bash 
  # root path of the project
  gradle jibDockerBuild
  # run container locally
  docker run -p 8080:8080 jrrl/parcel-delivery-calculator:latest
```