# Team03

## How to run t2_SequentialSolution

- Go to the following directory `t2_SequentialSolution/src/main/java`
- Open the `Main` class
- Run the `Main` class

You will find the results in the terminal.

## How to run t3_SharedMemorySolution

- Go to the following directory `t3_SharedMemorySolution/src/main/java`
- Open the `Main` class
- Run the `Main` class

You will find the results in the terminal.

## How to run t4_DistributedMemorySolution

- Go to the following directory `t4_DistributedMemorySolution/src/main/java`
- Open the `RmiMain` class
- Run the `RmiMain` class
  - The terminal will ask the following questions
  - Please provide the number of worker processes?
    - Here you can enter a number for example: `10`
  - Please provide the number of worker processes?
    - Here you can enter a number for example: `10`

You will find the results in the terminal.

## How to run benchmarks 

### t2_SequentialSoltuion

- Go to the following directory `t2_SequentialSolution/src/main/java`
- Open the `Book` class
  - Change the `bookFilePath` String to the desired amount of books resource file.
- Go to the following directory `t3_SequentialSolution/src/test/java`
- Open the `SequentialBenchmarkTest` class
- Change `fileName` to your own choice
- to start the benchmark:
  - Run `SequentialBenchmarkTest` for a full benchmark run
  - Run `testKnapSackSizeXXX` for a single benchmark run 

The results will be shown in the terminal and be saved in the csv file

### t3_SharedMemorySolution

- Go to the following directory `t3_SharedMemorySolution/src/main/java`
- Open the `Book` class
  - Change the `bookFilePath` String to the desired amount of books resource file.
- Go to the following directory `t3_SharedMemorySolution/src/test/java`
- Open the `ParallelBenchmarkTest` class
- Change `fileName` to your own choice
- to start the benchmark:
  - Run `ParallelBenchmarkTest` for a full benchmark run
  - Run `testKnapSackSizeXXX` for a single benchmark run

The results will be shown in the terminal and be saved in the csv file

### t4_DistributedMemorySolution

- Go to the following directory `t4_DistributedMemorySolution/src/main/java`
- Open the `Book` class
  - Change the `bookFilePath` String to the desired amount of books resource file.
- Go to the following directory `t3_DistributedMemorySolution/src/test/java`
- Open the `DistributedBenchmarkTest` class
- Change `fileName` to your own choice
- to start the benchmark:
  - Only run the tests named `TestKnapSackSize` + `Number`

The results will be shown in the terminal and be saved in the csv file

## How to run ServerClientSolution

With in this solution you need to make sure to be able to run multiple servers of the same class in Intellij.
To make this possible:

- go to the directory `ServerClientSolution/src/main/Server`
- Click on the green arrow and select `Modify run configuration`. Now a venster will popup.
- In this venster you need to click on Modify options.
- In this list you need to select `Run multiple instances`

Now you are able to run multiple instances of the `Server` class. So first start up the server instances.

1. Make sure the `SERVICE_PORT` of the first instance needs to be `49990`
2. Press the green arrow to start the server instance.
3. Repeat step 1 and 2, three times but make sure to change the `SERVICE_PORT` to `49991`, `49992` and `49993`

- Now you can go to the `Client` class of the same directory.
- Start the `main` method of the `Client` class.

You will find the results in the terminal.
