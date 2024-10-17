# Group 5 - Counting and Sorting
The "Counting and Sorting" project endeavours to create an inventory management and stock replenishment system with a user friendly interface capable of real-time updates, enabling accurate reflections of stock availability and consumption patterns to minimise time and resources required for stock maintenance. 

## Table of Contents
 - [Software & Hardware Requirements](#item-one)
 - [Installation Instructions](#item-two)
 - [Deployment Instructions](#item-three)
 - [Versioning Strategy](#item-four)
 - [Pull Request Strategy](#item-five)
 - [Additional Notes](#item-six)
 - [Team Members](#item-seven)
 - [Gen AI Statement](#item-eight)



<a id="item-one"></a>

## Software & Hardware Requirements
- Operating System: Windows, macOS, or Linux.
- Desktop/Laptop: The project is web-hosted and can run on any desktop browser.
- Java JDK: Java JDK 8 or greater is required.
- Apache Maven: Use the latest version of Maven for project management.
- Storage: At least 500MB of free space for the Maven dependencies.
- Web Browser: Any modern browser (Chrome, Firefox, Safari).

<a id="item-two"></a>

## Installation Instructions
1. Install Maven: Download Maven from [Maven's official website](https://maven.apache.org/download.cgi) and follow the instructions. Ensure Java JDK is installed and JAVA_HOME is added to the system's environment variables.
2. Clone the Repository: 
```git clone https://github.com/your-username/inventory-management-system.git```
3. Navigate to the Project Directory: ```cd inventory-management-system/```
4. Clean and Install Dependencies: ```mvn clean install```
5. Run the Application: ```mvn spring-boot:run``` <br/>
After running the above command, the application will be accessible at http://localhost:8080.

<a id="item-three"></a>

## Deployment Instructions
### Running the Application
To run the project, ensure Java JDK 8+ is installed. You can run the application using the following commands: <br/>
```mvnw``` (Windows) or ```./mvnw``` (Mac/Linux).
<br/>
Then open your browser and visit http://localhost:8080.

### Deploying to Production
For a production build: ```mvnw clean package -Pproduction``` <br/>
This will generate a JAR file that can be executed: ```java -jar target/liquor-store-1.0-SNAPSHOT.jar```

### Docker Deployment
To build and run the application with Docker: <br/>
1. Build the Docker image:
   ```docker build . -t liquor-store:latest```
2. Run the container:
   ```docker run -p 8080:8080 liquor-store:latest```

### API Documentation
To view the API documentation for the inventory management system, follow these steps: <br/>

#### Start the API Server:
Before you can access the documentation, ensure the API server is running. Navigate to the api directory within the inventory-management-system project: <br/>
```bash cd inventory-management-system/api ``` <br/>
Start the server using Uvicorn: <br/>
```bash uvicorn app.main:app --reload``` <br/>
The server will start on http://127.0.0.1:8000.

#### Accessing Redoc Documentation:
The Redoc-generated documentation provides a comprehensive view of the API endpoints and their details. Once the server is running, open your web browser and navigate to:
http://127.0.0.1:8000/redoc <br/>
This page will display the API documentation in a user-friendly, interactive format. <br/>
#### Accessing Swagger UI:
For a more interactive approach to testing the API endpoints, Swagger UI is available. You can access it via:
http://127.0.0.1:8000/docs <br/>
Here, you can execute API requests directly from the browser, making it easier to test and understand the API functionality.


<a id="item-four"></a>

## Versioning Strategy
We are adopting Semantic Versioning: [MAJOR].[MINOR].[PATCH] <br/>
MAJOR: Changes when incompatible API modifications are made. <br/>
MINOR: Changes when functionality is added in a backward-compatible manner. <br/>
PATCH: Changes when backward-compatible bug fixes are made. <br/>
Each release should be tagged in the Git repository with the version number to ensure traceability.
<br/>
The current verson number is 4.1.1

<a id="item-five"></a>

## Pull Request Strategy
1. Create a New Branch. ```git checkout -b feature/my-new-feature```
2. Commit Changes using the format: [Feature/Refactor/Fix] [Scope] [Description].
3. Open a Pull Request. Submit your pull request (PR) with an appropriate title and description. Be sure to include appropriate version tags.
4. Delete Source Branch after the PR is merged to maintain cleanliness in the repository.

<a id="item-six"></a>

## Additional Notes
### Project structure
- ```MainLayout.java``` in ```src/main/java``` contains the navigation setup (i.e., the
side/top bar and the main menu). This setup uses [App Layout](https://vaadin.com/docs/components/app-layout).
- ```views``` package in ```src/main/java``` contains the server-side Java views of your application.
- ```views``` folder in ```frontend/``` contains the client-side JavaScript views of your application.
- ```themes``` folder in ```frontend/``` contains the custom CSS styles.

### Useful links
- Read the [vaadin documentation](https://vaadin.com/docs).
- Follow the [vaadin tutorial](https://vaadin.com/docs/latest/tutorial/overview).
- Create new [vaadin projects](https://start.vaadin.com/).
- Search [UI components and their usage examples](https://vaadin.com/docs/latest/components).
- View [use case applications](https://vaadin.com/examples-and-demos) that demonstrate Vaadin capabilities.
- Build any UI without custom CSS by discovering Vaadin's set of [utility classes](https://vaadin.com/docs/styling/lumo/utility-classes).
- Find a collection of [solutions to common use cases](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).

<a id="item-seven"></a>

## Team Members
- Jun Hao Ng - jngg0122@student.monash.edu
- Tye Bram Leigh Samuels - tsam0016@student.monash.edu
- Navya Balraj - nbal0016@student.monash.edu
- Behnam Mozafari - bmoz0002@student.monash.edu
- Reuben Gue - rgue0001@student.monash.edu
- Dylan Redman - dred0006@student.monash.edu
- Andrew Rudnytsky - arud0001@student.monash.edu
- Linuka Ekanayake - leka0001@student.monash.edu
- Garv Vohra - gvoh0002@student.monash.edu
- Jesse Gordon - jgor0013@student.monash.edu
- Ismail - Ihas0007@student.monash.edu
- Abdus Sami - asam0025@student.monash.edu
- Erin Koay - ekoa0001@student.monash.edu
- Yokabit Fesshaye - yfes0001@student.monash.edu

<a id="item-eight"></a>

## Gen AI Statement
This document has been created with the assistance of Generative Artificial Intelligence (Gen AI) technology. Specifically, Gen AI has been employed to create the structure of the markdown document.