# Kafka Concurrent Batch Message Processor
This project demonstrates a solution for processing Kafka messages concurrently in a constrained environment with limited Kafka partitions.

## Overview
This library offers a KafkaBatchConsumer class that addresses the limitations of single-threaded processing when Kafka topic partitions are restricted. It utilizes batch processing and concurrent execution within a pod to efficiently utilize resources and overcome limitations.

## Key Features
- Built with Spring Boot 3.4.0 for modern Java microservices development.
- Leverages Kafka for message-driven architecture, enabling both message production and consumption.
- Implements batch processing to handle high-throughput scenarios.
- Processes messages concurrently within a pod using Java's Callable and ExecutorService.
- Provides manual acknowledgment of Kafka messages for reliable and flexible processing.

## Problem Statement
Scenarios with:

- High message latency (e.g., ~5000ms per message).
- Limited Kafka partitions (e.g., 2) restricting consumer concurrency across pods.
- Scalable deployments with numerous Kubernetes pods (e.g., 20+).

This configuration leads to underutilized resources as only a few pods can consume messages concurrently.

## Solution Overview
- Batch Consumption: Fetch messages in batches (e.g., 5).
- Concurrent Processing: Divide each batch into separate tasks for parallel processing using a thread pool.
- Manual Acknowledgment: After processing all tasks, compute the acknowledgment index and send a manual acknowledgment to Kafka.

This approach allows for full resource utilization and concurrent processing regardless of limited partitions.

## How It Works
Message Consumption: Consumes messages from Kafka in batches.
Parallel Processing: Each message in the batch is processed concurrently using a thread pool.
Acknowledgment Handling: Once all messages are processed, sends a manual acknowledgment to Kafka to commit the offset.

## Usage
Extend the KafkaBatchConsumer class for customized batch processing logic. The implementation handles batching and retries failed messages in the following batch.

## Technologies
- Spring Boot 3.4.0
- Apache Kafka
- Java ExecutorService

## Running the Project
#### Prerequisites
1. Docker installed on your machine.

2. Java SDK 21.

#### Steps to Run
1. **Start Kafka**:
Run the following command to start Kafka using Docker:
   ```sh
   docker run -d --name=kafka -p 9092:9092 apache/kafka
   ```
2. **Run the Application**:
Execute the application using your preferred method (e.g., via IntelliJ IDEA, command line, etc.).
3. **Produce Messages**:
Send an HTTP request to produce messages. For example, to produce `2000` messages, use:
   ```sh
   curl "http://localhost:9090/kafka-message?count=2000"
   ```
This command will produce `2000` messages, which the application will then start to consume.

## Use Case
This library is ideal for:

- Scenarios with limited Kafka partitions but requiring high concurrency.
- Applications needing reliable message acknowledgment.
- Kubernetes deployments with numerous pods.

## Contributing
Pull requests are welcome. Please open an issue for major changes before submitting a pull request.

## Contact
For any questions or inquiries, please contact [malvandi.m@gmail.com].


