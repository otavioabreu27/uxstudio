# UX Studio Technical Challenge
### Backend Developer Technical Assessment

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Next.js](https://img.shields.io/badge/next.js-%23000000.svg?style=for-the-badge&logo=nextdotjs&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)


[![Backend CI](https://github.com/otavioabreu27/uxstudio/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/otavioabreu27/uxstudio/actions/workflows/backend-ci.yml)
[![Frontend CI](https://github.com/otavioabreu27/uxstudio/actions/workflows/frontend-ci.yml/badge.svg)](https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/frontend-ci.yml)
![Digital Ocean](https://img.shields.io/badge/Digital_Ocean-%230080FF.svg?style=flat-square&logo=digitalocean&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat-square&logo=docker)
![Powered by Cloudflare](https://img.shields.io/badge/Powered%20by-Cloudflare-F38020?style=flat-square&logo=cloudflare&logoColor=white)

## 🏗 Architectural Decisions & Philosophy

As the sole backend developer in a cross-functional team alongside designers and frontend developers, my primary architectural goal was **Autonomy** and **Developer Experience (DX)**.

The system was designed to prevent the backend from becoming a bottleneck. By prioritizing a clear contract and easy integration, the frontend team can iterate with agility without constant dependency on backend synchronization.

### Key Technical Strategies

* **API-First Approach:** The architecture is "pluggable." Integration is driven by strict API documentation (Swagger), allowing the frontend to consume endpoints seamlessly. Switching environments is as simple as updating an environment variable.
* **Decoupled Integration:** The focus was on reducing friction. Comprehensive documentation and monitoring allow other team members to understand the backend state without needing to dig into the backend code.

## 🔗 Project Links

* **Live Application:** [https://uxstudio.unilaunch.org/](https://uxstudio.unilaunch.org/)
* **API Documentation (Swagger UI):** [https://uxstudio-back.unilaunch.org/swagger-ui/index.html](https://uxstudio-back.unilaunch.org/swagger-ui/index.html)
* **System Monitoring:** [https://uxstudio-back.unilaunch.org/](https://uxstudio-back.unilaunch.org/)

## 📝 Implementation Notes & Trade-offs

### Error Handling Strategy
A strategic design choice (currently in the backlog) is the centralization of exception handling to return a standardized `userMessage` field in API responses.
* **Goal:** Encapsulate business logic errors within the backend, allowing frontend components to simply display messages without parsing complex error codes.
* **Status:** Due to the strict time constraints of the challenge, I prioritized core feature delivery and stability over this enhancement.

### Infrastructure & Hosting
To demonstrate DevOps capabilities and resourcefulness, this project is hosted on a self-managed infrastructure.
* **Domain:** The application runs on `unilaunch.org`, a personal project environment.
* **Hardware:** Hosted on a Raspberry Pi cluster within my homelab, utilizing Docker for containerization.
* **Benefit:** This setup replicates a production-like cloud environment, where developers can access the "testing" backend app directly if they want to (0 effort on infra and backend stuff).

You can view this project manifest at the root domain: [unilaunch.org](https://unilaunch.org).



## 🚀 Running the Project Locally



To run the application on your local machine, follow these steps:



### 1. Run the Infrastructure



This project uses Docker Compose to set up the necessary infrastructure, which includes a MongoDB database and a MinIO object storage service.



Make sure you have Docker and Docker Compose installed, and then run:



```bash

docker-compose -f infra.yaml up -d

```



### 2. Run the Backend



The backend is a Spring Boot application. To run it, navigate to the `backend` directory and use the Gradle wrapper:



```bash

cd backend

./gradlew bootRun

```



The backend server will start on `http://localhost:8080`.



### 3. Run the Frontend



The frontend is a Next.js application. To run it, navigate to the `front` directory, install the dependencies, and start the development server:



```bash

cd front

pnpm install

pnpm dev

```



The frontend development server will be available at `http://localhost:3000`.

#### Alternative for Frontend Developers (using testing backend)

Frontend developers can also run the application by pointing to the testing backend. To do this, create a `.env.local` file in the `front` directory with the following content:

```
NEXT_PUBLIC_API_BASE_URL=https://uxstudio-back.unilaunch.org
```

Then, run the frontend as usual:

```bash
cd front
pnpm install
pnpm dev
```

