import com.mongodb.client.MongoClients;
import groovy.xml.XmlSlurper;
import org.bson.Document;

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

def mongoClient = MongoClients.create("mongodb://" + mongoDatabaseHost + ":" + mongoDatabasePort)
def collection = mongoClient.getDatabase(mongoDatabaseName).getCollection('coffeeshop')
collection.drop()

def xmlSlurper = new XmlSlurper().parse(new File(coffeeShopFile))

xmlSlurper.node.each { child ->
    Map coffeeShop = [
        'openStreetMapId': child.@id.text(),
        'location': ['coordinates': [Double.valueOf(child.@lon.text()), Double.valueOf(child.@lat.text())], 'type': 'Point']]

    child.tag.each { node ->
        coffeeShop.put(node.@k.text(), node.@v.text())
    }

    if (coffeeShop.name != null) {
        println coffeeShop
        collection.insertOne(new Document(coffeeShop))
    }
}

println "\nTotal coffee shops imported: ${collection.countDocuments()}"

collection.createIndex(new Document('location', '2dsphere'))
