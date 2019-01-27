@echo off

setlocal

echo.
echo ### Some useful commands:
echo mongo microcoffee
echo show databases
echo show collections
echo db.coffeeshop.count()
echo db.order.find()
echo.

mongod --dbpath /var/mongodb/microcoffee

endlocal
