Developed a Spring Boot-based backend application for the
Students platform, designed to serve as the core backend system
enabling users to create accounts and enroll in courses of interest.
The system is composed of four microservices, each responsible for
a distinct task:

API Gateway – handles and routes incoming HTTP requests.

Student Service – manages user authentication and
authorization.

Course Service – manages course data and content.

Eureka Service – handles service discovery and load balancing.

Microservices communicate with each other via Feign clients and
are registered using Eureka for dynamic service discovery.

The application integrates with multiple storage solutions:

PostgreSQL – for storing user data

MongoDB – for storing course content and metadata

Azure Blob Storage – for storing profile pictures, course images,
and video content.

Authentication and authorization across all services are handled
centrally via the Student Service, using Spring Security.

![image](https://github.com/user-attachments/assets/ca19aae2-1b39-4b91-a34e-3a17853e14a8)
