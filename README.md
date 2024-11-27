A simulation of the master-slave architecture for operating systems, demonstrating inter-process communication, task delegation, and synchronization. This project showcases core concepts in operating system design through a simplified implementation.

Features
Master process manages and delegates tasks to slave processes.
Simulates workload distribution and task synchronization.
Demonstrates inter-process communication (IPC) using queues, pipes, or sockets.
Tracks task progress and handles completion or failure.
Technologies Used
Language: Java
IPC Mechanism: socket programming

How It Works
Master Process:
Initializes resources and spawns slave processes.
Distributes tasks to slaves using an IPC mechanism.
Slave Processes:
Receive tasks from the master.
Execute tasks and report results back to the master.
Synchronization:
Ensures tasks are executed and results are handled in a coordinated manner.
Example Usage

Observe the output:
Logs for task assignment, progress, and completion.
Statistics summarizing task distribution and performance.
Customization
You can modify the following parameters in the code (or a configuration file if available):

Number of slave processes.
Task complexity and duration.
Logging verbosity.
