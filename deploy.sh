#!/bin/bash

# Step 1: Navigate to your project directory
cd inventory-management-system

# Step 2: Clean, build, and package the project using Maven with the production profile
mvn clean package -Pproduction

# Step 3: Deploy the JAR file to Heroku
heroku deploy:jar target/liquor-store-1.0-SNAPSHOT.jar --app mysterious-shelf-74202

# Step 4: Output deployment status
if [ $? -eq 0 ]; then
  echo "Deployment successful!"
  echo "Visit your app at: https://mysterious-shelf-74202.herokuapp.com/"
else
  echo "Deployment failed!"
  heroku logs --tail --app mysterious-shelf-74202
fi
