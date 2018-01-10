println "Hello world"
println "groovy.version = " + project.properties['groovy.version']
println "dbhost = " + dbhost
println "dbport = " + dbport
println "dbname = " + dbname
println "shopfile = " + shopfile

if (!dbhost) {
    println "Missing dbhost property"
    return
}

if (!dbport) {
    println "Missing dbport property"
    return
}

if (!dbname) {
    println "Missing dbname property"
    return
}

if (!shopfile) {
    println "Missing shopfile property"
    return
}

def mongoDatabaseHost = dbhost
def mongoDatabasePort = dbport
def mongoDatabaseName = dbname
def coffeeShopFile = shopfile

println "mongoDatabaseHost = " + mongoDatabaseHost
println "mongoDatabasePort = " + mongoDatabasePort
println "mongoDatabaseName = " + mongoDatabaseName
println "coffeeShopFile = " + coffeeShopFile

println "Finished"
