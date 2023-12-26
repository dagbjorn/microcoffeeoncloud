@echo off

setlocal

echo.
echo ### Some useful commands:
echo mongo microcoffee
echo show databases
echo show collections
echo db.coffeeshop.countDocuments()
echo db.order.find()
echo.

mongod --version
mongod --dbpath /var/mongodb/microcoffee

endlocal
