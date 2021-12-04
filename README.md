### Note: Commit History users Ubuntu is our AWS Linux User, Chunzhi Xu accidentally push changes without checking the username when debugging on AWS.
- # General
    - #### Team#: **Fall 2021 Team 6** 
    
    - #### Names: Chunzhi Xu & Haoting Ni
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:
     
    ```
    - On AWS Server clone the project1
    git clone https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-6.git
    
    - Direct to project folder
    
    cd cs122b-fall21-team-6/Fablix
    
    - Build the war file

    mvn package
  
    - Copy war file to tomcat to deploy
   
    sudo cp ./target/*.war /var/lib/tomcat9/webapps/
    ```

    - #### Collaborations and Work Distribution:
     
        
     ```
      - CurtisXuCAD (Chunzhi Xu)
       Build movie list page
       Beautify table
       GitHub setup
       AWS setup
       Create demo
       Bug fixing
       Beautify Login, Main Page, Movie List Page
       Jump Function using session
       Query optimization
       Pagination
       Sorting
       HTTPS
       Password Encryption
       XML Parsing
       Prepared Statedment
       Fixing Query & Statedment
       Android App -- Fablix Mobile
       Fixing Pagination & Sorting for full-text search result
       log Processing script
       JMeter Time Report
       Log files
     ```

     
     ```
     - Silence-silence-silence (Haoting Ni)
       Single Movie Page
       Single Star Page
       Jump Function
       Readme Creation 
       Beautify Pages
       Creat_table.sql
       Main Page 
       Login Page
       Browsing and Search Functionality
       Shopping Cart
       Payment Page
       Place Order
       Confirmation Page
       Add to Cart
       Beautify Shopping Cart, Payment Page, Confirmation Page
       reCAPTCHA
       Prepared Statedment
       Employee Dashboard
       Full-text Search
       Autocomplete
       Connection pooling
       Master/Slave
       Loading balacing
     ```
- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
<a href="src/PaymentServlet.java">PaymentServlet.java</a>
<a href="src/DashboardServlet.java">DashboardServlet.java</a>
<a href="src/ConfirmationServlet.java">ConfirmationServlet.java</a>
<a href="src/IndexServlet.java">IndexServlet.java</a>
<a href="src/LoginServlet.java">LoginServlet.java</a>
<a href="src/MainServlet.java">MainServlet.java</a>
<a href="src/MoviesServlet.java">MoviesServlet.java</a>
<a href="src/PaymentServlet.java">PaymentServlet.java</a>
<a href="src/rDashboardServlet.java">rDashboardServlet.java</a>
<a href="src/SingleMovieServlet.java">SingleMovieServlet.java</a>
<a href="src/SingleStarServlet.java">SingleStarServlet.java</a>
<a href="WebContent/META-INF/context.xml">context.xml</a>
<a href="WebContent/WEB-INF/web.xml">web.xml</a>

- #### Explain how Connection Pooling is utilized in the Fabflix code.
```
-In the context.xml file, first define all the database resource you want to use.
-In the web.xml file, register all the database resource you defined in the context.xml.
-On top of the servelet file what you want to use to connect to the database, define a datasource object.

 public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/nameOfdatasource");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
-use getconnection to connect to databases.
Connection conn = dataSource.getConnection()
- The connection will be reused by the clients in this way.
```
- #### Explain how Connection Pooling works with two backend SQL.
```
- Since we have create two different datasources in the context.xml file, we could use different datasources depending on the situation.
- Because there is no register on the read operations, we could define it as localhost mysql. So it could connect either one of the backend SQL.
- And we define another connection to master instance for write operations.
- The connections will be reused depending on the types of the operations.
```

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

<a href="src/PaymentServlet.java">PaymentServlet.java</a>
<a href="src/DashboardServlet.java">DashboardServlet.java</a>
<a href="src/ConfirmationServlet.java">ConfirmationServlet.java</a>
<a href="src/IndexServlet.java">IndexServlet.java</a>
<a href="src/LoginServlet.java">LoginServlet.java</a>
<a href="src/MainServlet.java">MainServlet.java</a>
<a href="src/MoviesServlet.java">MoviesServlet.java</a>
<a href="src/PaymentServlet.java">PaymentServlet.java</a>
<a href="src/rDashboardServlet.java">rDashboardServlet.java</a>
<a href="src/SingleMovieServlet.java">SingleMovieServlet.java</a>
<a href="src/SingleStarServlet.java">SingleStarServlet.java</a>
<a href="WebContent/META-INF/context.xml">context.xml</a>
<a href="WebContent/WEB-INF/web.xml">web.xml</a>
 - #### How read/write requests were routed to Master/Slave SQL?
<a href="src/PaymentServlet.java">PaymentServlet.java</a>
<a href="src/rDashboardServlet.java">rDashboardServlet.java</a>
Thess two web pages are the place that we implement write operations to the SQL. Because we could both write and read to master instance. We simply defined a connection to master instance on the top of the file. Every operations come from these two website will be routed to Master SQL.

For rest of the websites, all the operations are read. So it does not master whether it is connect to Master or Slave. We simply make it connect to localhost.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


---
## Video Demo Link
**https://youtu.be/ImmVItD0py4**
## Application URL
**https://ec2-54-151-116-40.us-west-1.compute.amazonaws.com:8443/fablix/**



## Substring matching design
- To search Title, Director, Stars:
 ```
 Pattern: LIKE %ABC%  (ABC is key word enter in the text bar)
 Any movies contain all key words entered in the text bars.
 
 If title has key word A, director has key word B, stars has key word C:
 Mysql script: where movie.title Like %A% and movie.director LIKE %B% and stars.name LIKE %C%
 ```
## Prepared Statement
We use Prepared Statement mainly in MoviesServlet.java, every url parameter and user input will finally put into a prepared statement which protect the database from sql attack.
<a href="src/PaymentServlet.java">PaymentServlet.java</a>

Others:
<a href="src/DashboardServlet.java">DashboardServlet.java</a>
<a href="src/ConfirmationServlet.java">ConfirmationServlet.java</a>
<a href="src/IndexServlet.java">IndexServlet.java</a>
<a href="src/LoginServlet.java">LoginServlet.java</a>
<a href="src/MoviesServlet.java">MoviesServlet.java</a>
<a href="src/rDashboardServlet.java">rDashboardServlet.java</a>
<a href="src/SingleMovieServlet.java">SingleMovieServlet.java</a>
<a href="src/SingleStarServlet.java">SingleStarServlet.java</a>
## Two parsing time optimization strategies
1. I load the original data we need from database to help use check if the new data is already exist or not immediately when we finish reading each element. Thus, we need less query when we process the data.

2. I use the LOAD DATA LOCAL INFILE feature to load everything we need to add to the database. I first create several csv file for different tables in database and store the new data into these csv file. After creating the csv file, I only need to use LOAD DATA LOCAL INFILE feature to load the data at once, which hugely improve the parsing time.

## Inconsistent data report

[Inconsistent Report](xml_parser/inconsistency_report.txt)



