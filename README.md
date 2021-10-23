# CS122B-Fall21-Team-6
This is a UCI CS122B Project1 made by **Fall 2021 Team 6** (Chunzhi Xu & Haoting Ni)
### Note: Commit History users Haoting Ni and Silence-silence-silence are done by the same person Haoting Ni. Only for project 1, 2 because of mistaken push from local desktop.
---
## Video Demo Link
**https://youtu.be/VxH_fKQ9mSs**
## Application URL
**http://ec2-3-101-58-84.us-west-1.compute.amazonaws.com:8080/cs122b-fall21-team-6-project1/**

## How to deploy your application with Tomcat
- On AWS Server clone the project1
 ```
 git clone https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-6.git
 ```
- Direct to project folder
 ```
 cd cs122b-fall21-team-6/
 ```
- Build the war file
 ```
 mvn package
 ```
- Copy war file to tomcat to deploy
 ```
 sudo cp ./target/*.war /var/lib/tomcat9/webapps/
 ```
## Substring matching design
- To search Title, Director, Stars:
 ```
 Pattern: LIKE %ABC%  (ABC is key word enter in the text bar)
 Any movies contain all key words entered in the text bars.
 
 If title has key word A, director has key word B, stars has key word C:
 Mysql script: where movie.title Like %A% and movie.director LIKE %B% and stars.name LIKE %c%
 ```

## Contribution
- CurtisXuCAD (Chunzhi Xu)
```
  Create movie list page
  Beautify table
  GitHub setup
  AWS setup
  Create demo
  Bug fixing
```

- Silence-silence-silence (Haoting Ni)
```
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
```
