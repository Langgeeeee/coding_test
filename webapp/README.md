Please note the following instructions before you test the program:

1, You can download the program file "ImageCompressService", create a new java project in IDE to start the application. or just start it by    
   command "java ImageCompressService.java";

2, For upload image to the application, we use "curl command" to simulate a front-end form submission. please note that running the command
   in the directory where the image is located.

   The command:
   curl -X POST -F "image=@test.jpg" http://localhost:8080/api/upload --output compressed-test.jpg
