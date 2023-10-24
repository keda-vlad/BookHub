# Bookstore API: Enhancing Your Bookstore Experience

Welcome to the Bookstore API project! Our powerful and secure application, developed using Java Spring Boot, aims to enrich online book purchases for all book enthusiasts. We have combined various features and advanced technologies to provide users with a secure, comprehensive and convenient e-commerce platform. Below, we will introduce you to the key aspects and functional capabilities of our application.

## Features:

### Security:

Bookstore uses JWT tokens to ensure secure authentication and authorization. This guarantees that only verified users can access sensitive features.

### Books and Categories:

Administrators have the functionality to add and update categories and books in the database. Users can make use of extensive book search functionality by categories, authors, titles, or prices with pagination and sorting.

### Shopping Cart:

Our users can add books to the shopping cart. The cart is fully functional, allowing users to view, edit, and complete purchases before proceeding to checkout.

### Order Management:

Administrators can manage order statuses, and users can create orders, view their previous orders, and their statuses.

## Used Technologies:

- Spring Boot: Dependency and library management through Maven.
- Spring Data JPA: Executing CRUD operations on data models.
- Spring Security: Ensuring application and user data security.
- Spring MVC: Developing a web application using the Model-View-Controller (MVC) pattern for better code organization and separation of business logic, views, and control.
- MapStruct: Efficient mapping of models to Data Transfer Objects (DTOs).
- Docker: Containerization for easy deployment and scalability.
- OpenAPI: Documenting our RESTful API to provide clear information about available resources, requests, and response formats.
- Liquibase: Managing and versioning the database schema for easy schema changes and data schema consistency in different environments.

## Functionality:

In our API, functionality is divided for Users and Admin:

| Controller               | User                                                     | Admin                              |
|--------------------------|----------------------------------------------------------|------------------------------------|
| BookController           | Book search with filtering, pagination and specification | Read, update, delete functionality |
| CategoryController       | Category and books by categories search                  | Read, update, delete functionality |
| AuthenticationController | Registration, authentication                             | Ability to change user roles       |
| ShoppingCartController   | CRUD operations on the shopping cart                     | -                                  |
| OrderController          | Order creation, orders viewing                           | Order status management            |


## Installation
### Prerequisites

Before you begin, ensure you have the following prerequisites installed on your system:

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://docs.docker.com/get-docker/)

Follow these steps to set up and run the Book Store API on your local machine:

**Clone the Repository:**

   Via IDE:
    - Open IntelliJ IDEA.
    - Select "File" -> "New Project from Version Control."
    - Paste the link: https://github.com/keda-vlad/bookstore.git
    - Run the following commands:

        mvn clean package
        docker build -t your_image_name
        docker-compose up --build

   Via the console:
    - Execute the command: `git clone https://github.com/keda-vlad/bookstore.git`
    - Run the following commands:

        mvn clean install
        docker build -t your_image_name
        docker-compose up --build

## Usage
   If you want test Api without installation go to [link](http://ec2-54-82-60-253.compute-1.amazonaws.com/api/swagger-ui/index.html#/Authentication%20management/login).

   
   Or you can use Postman, if you completed installation:

   - Open Postman.
   - Import the [file](book-store.postman_collection.json) with requests.
## Video Presentation
In this [video](https://www.loom.com/share/ac332337b27346a8812336daa807c97a), you'll get a comprehensive demonstration of how the application functions. We'll explore the intricacies of searching for books and categories, the admin's capacity to modify them, the features available to users, and the mechanics behind the shopping cart and order processes.
## Contribution

   ### Issues and Solutions

   **Issue 1: N+1 Problem**
    - Solution: Use `@Query(join fetch)` or `@EntityGraph` annotations for methods with specifications.

   **Issue 2: Authentication Security**
    - Solution: We implemented Spring Security and JWT tokens for robust protection.

   **Issue 3: Exception Handling**
    - Solution: We developed a global exception handler and specific exceptions to improve error reporting.

# Conclusion:

   The Bookstore API provides a robust foundation for building a book-selling platform. We welcome contributions from the community to enhance the Bookstore project. Whether you want to fix a bug, improve an existing feature, or propose a new one, your contributions are valuable to us.

   ### Upcoming Expected Updates:

   I plan to add user recommendation functionality.

## Contact Us:

   For any inquiries or suggestions, please email us at vladyslav.keda@gmail.com.

   Thank you for your interest in the Bookstore API project and your contribution to its development!
