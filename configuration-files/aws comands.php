AWS Comands :
sudo yum update -y
sudo yum install -y httpd
sudo systemctl start httpd
sudo systemctl enable httpd
sudo su - // need to root user
echo "hello master developer">/var/www/html/index.html
sudo systemctl status httpd