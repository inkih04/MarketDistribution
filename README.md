
# PROP Group 23.2
Programming project, Group 23, subgroup 2 <br>Teacher: Sergio Álvarez Napagao ([salvarez@cs.upc.edu]()).

## Members of the group

- Víctor Díez Serrano ([victor.diez@estudiantat.upc.edu]())
- Max Estrade Pey  ([max.estrade@estudiantat.upc.edu]())
- Víctor Llorens Ramos([victor.llorens@estudiantat.upc.edu]())
- Sergio  Shmyhelskyy Yaskevych ([sergio.shmyhelskyy@estudiantat.upc.edu]()).
 
## Classes Implemented by Each Member

### Domain Layer

| Member                  | Classes Implemented                                                                 |
|-------------------------|-------------------------------------------------------------------------------------|
| Max Estrade             | AbstractAlgorithm, BruteForceAlgorithm, HillClimbingAlgorithm, Distribution |
| Víctor Díez             | InputManager, ControllerDomain, DriverControllerDomain, Shelf                       |
| Víctor Llorens          | Similarity, SimilarityManager, ShelfManager, Shelf, Pair |
| Sergio Shmyhelskyy      | Product, ProductList, ProductManager, DistributionManager DriverProductManager, UserManager, UserSet, User, CurrentUser|

### Persistence Layer

| Member                  | Classes Implemented                                                                 |
|-------------------------|-------------------------------------------------------------------------------------|
| Max Estrade             | -                                                                                   |
| Víctor Díez             |  -                                                                                   |
| Víctor Llorens          | UserManagerData, ControllerPersistence                                                                                   |
| Sergio Shmyhelskyy      | ProductManagerData, UserManagerData , PersistenceController                                               |

### Presentation Layer

| Member                  | Classes Implemented                                                                 |
|-------------------------|-------------------------------------------------------------------------------------|
| Max Estrade             | -                                                                                   |
| Víctor Díez             | PresentationController, LoginView, ShelfView, MenuView, ProductCatalogView                                                                                  |
| Víctor Llorens          |  CreateShelfView, GenerateDistributionView, ManageDistributionsView, ShelfView, ShowDistributionView                                                                                  |
| Sergio Shmyhelskyy      | BasicView, AddProductView, ProductCatalogView, ProductListMenuView, ProductListView, SimilaritiesDialog, OperationLogView                              |

## APP Menu

<br>
<div align="center" style="margin-bottom: 40px;">
  <img src="https://github.com/inkih04/SuperMarketDistribution/blob/main/pictures/optionsMenu.png" alt="Options Menu">
</div>

<br>

<div align="center" style="margin-bottom: 40px;">
  <img src="https://github.com/inkih04/SuperMarketDistribution/blob/main/pictures/distributions.png" alt="Distributions">
</div>

<br>

<div align="center" style="margin-bottom: 40px;">
  <img src="https://github.com/inkih04/SuperMarketDistribution/blob/main/pictures/Product%20Catalog_001.png" alt="Menu Image">
</div>

## Directory elements

### DOCS:
Contains all the documentation of the project: use case diagram with description of each of them, complete static diagram of the conceptual data model in design version with a brief description of the attributes and methods of the classes, relationship of the classes implemented by each member of the team, description of the data structures and algorithms used to implement the functionalities of the delivery.

### EXE:
Executable files (*.class*) of all classes that allow testing the main implemented functionalities. There are subdirectories for each class type. 
There also are some test games for the application in the form of guides on how to use the application properly. These tests are in pdf format  at EXE/test_presentation.


### FONTS:
Code of the domain classes associated with the main functionalities implemented so far. It also includes the JUnit tests. All files are within the subdirectories that follow the package structure, so that the code is directly recompilable. It also includes the Makefile file.

### lib:
The external libraries that we have had to use for the JUnit tests are located here.

## Test the program

To run the program you need to go to the  /FONTS directory, which is where the *Makefile* of the program is located. To build the executable file you need to write *make* at the terminal

### Makefile Description

- `make all`

  Compile all classes in the system

- `make compile_classes`

  Compile all classes

- `make compile_tests`

  Compile tests with JUnit 4

- `make jarDomain`

  Create jar for Driver principal

- `make runDomainDriver`

  Run the driver of the controller domain

- 'make jarPresentation'

  Create jar for Driver principal

- `make runDriver`

    Run the driver of the controller presentation.
    THIS COMMAND EXECUTE THE MAIN DRIVER OF THE PROGRAM.

- `make clean`

  Remove all compiled files

- `make classclean`

  Remove all compiled files of the domain classes

- `make jars`

  Create all jars'

- `make test`

  Execute unitary tests with JUnit 4 and Mockito.

# PROP Project Statement

**Fall semester, academic year 2024/25**

## **Product distribution in a supermarket**

We have a supermarket and we want to find the **optimal distribution of products** offered so that customers buy more. To do this:

- We will assume that the **probability that a customer buys a product** increases if it is close to another related product (for example, if someone goes to buy beer and sees chips next to it, they will probably add them to their purchase).
- We assume that each pair of products has a **degree of similarity or relationship**, which the user knows and can provide.

### **Problem simplifications**

1. There is only one **circular shelf** in the supermarket.
2. The shelf has **a single level** (or several within this same shelf).
3. Based on the degree of similarity between products, the system must find the **optimal distribution** to maximize customer purchases.

---

## **Project requirements**

To ensure that the project is approved, at least the following functionalities must be implemented:

### **1. Product management**
- Allow adding, modifying, and deleting products from the system.

### **2. Management of similarities between products**
- Define and manage relationships between pairs of products.

### **3. Distribution calculation**
- The program must provide **at least two algorithms** to find the optimal distribution:
    - **Basic solution**: Brute force algorithm or a greedy algorithm.
    - **Approximation algorithm**: More information will be provided.
- The parameters of the algorithms must be configurable interactively.
- **Additional optimizations** will be valued.

### **4. Subsequent modification**
- The system must allow **manual modification** of the proposed solution.

### **5. Data import and definition**
- Data must be able to be defined:
    - Directly through the program.
    - Imported from a text file.
---

## **Optional extensions**

At the team's discretion, the system may include additional functionalities such as:

- Use of constraints in product distribution.
- Implementation of additional algorithms.
- Other improvements that increase the value of the project.

---

## **Quality criteria**

In addition to the general quality factors of any program (**design, coding, reusability, modifiability, usability, documentation, etc.**), the following will be especially valued:

- **Efficiency**: Performance of the solutions.
- **Flexibility**: Ability to adapt to new requirements.

---

## **Deliverables**

### **Main functionalities for the first deliverable**
- Implementation of the **two algorithms** to find the optimal distribution.

### **Reduction of functionalities for teams of 3 people**
- The option of **multiple levels** within the shelf should not be addressed.
- It is not necessary to optimize the solutions.

---

## **Delivery dates**

- **First deliverable:** Monday, November 18.
- **Second deliverable:** Monday, December 16.
- **Third deliverable:** Monday, December 23 (*interactive deliveries from January 7*).
