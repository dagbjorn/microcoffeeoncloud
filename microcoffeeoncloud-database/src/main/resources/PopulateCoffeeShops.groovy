import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

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
int mongoDatabasePort = Integer.parseInt(dbport)
def mongoDatabaseName = dbname
def coffeeShopFile = shopfile

println "mongoDatabaseHost = " + mongoDatabaseHost
println "mongoDatabasePort = " + mongoDatabasePort
println "mongoDatabaseName = " + mongoDatabaseName
println "coffeeShopFile = " + coffeeShopFile

def mongoClient = new MongoClient(mongoDatabaseHost, mongoDatabasePort)
def collection = mongoClient.getDB(mongoDatabaseName).getCollection('coffeeshop')
collection.drop()

def xmlSlurper = new XmlSlurper().parse(new File(coffeeShopFile))

xmlSlurper.node.each { child ->
    Map coffeeShop = [
        'openStreetMapId': child.@id.text(),
        'location': ['coordinates': [Double.valueOf(child.@lon.text()), Double.valueOf(child.@lat.text())], 'type': 'Point']]

    child.tag.each { theNode ->
        coffeeShop.put(theNode.@k.text(), theNode.@v.text())
    }

    if (coffeeShop.name != null) {
        println coffeeShop
        collection.insert(new BasicDBObject(coffeeShop))
    }
}

println "\nTotal coffee shops imported: $collection.count"

collection.createIndex(new BasicDBObject('location', '2dsphere'))

/*
 mongo CoffeeShop --host 192.168.99.100
 MongoDB shell version: 2.6.1
 connecting to: 192.168.99.100:27017/CoffeeShop
 > db.coffeeshop.count()
 93
 */
