import com.mongodb.client.MongoClients;
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

def mongoDatabaseHost = dbhost
int mongoDatabasePort = Integer.parseInt(dbport)
def mongoDatabaseName = dbname

println "mongoDatabaseHost = " + mongoDatabaseHost
println "mongoDatabasePort = " + mongoDatabasePort
println "mongoDatabaseName = " + mongoDatabaseName

def mongoClient = MongoClients.create("mongodb://" + mongoDatabaseHost + ":" + mongoDatabasePort)
def collection = mongoClient.getDatabase(mongoDatabaseName).getCollection('drinktypes')
collection.drop()

collection.insertOne([name: 'Americano', family: 'Coffee'] as Document)
collection.insertOne([name: 'Latte', family: 'Coffee'] as Document)
collection.insertOne([name: 'Tea', family: 'That other drink'] as Document)
collection.insertOne([name: 'Cappuccino', family: 'Coffee'] as Document)

println "Total drink types imported: ${collection.countDocuments()}"

collection = mongoClient.getDatabase(mongoDatabaseName).getCollection('drinksizes')
collection.drop()

collection.insertOne([name: 'Small'] as Document)
collection.insertOne([name: 'Medium'] as Document)
collection.insertOne([name: 'Large'] as Document)
collection.insertOne([name: 'X-Large'] as Document)
collection.insertOne([name: 'Supersized'] as Document)

println "Total drink sizes imported: ${collection.countDocuments()}"

collection = mongoClient.getDatabase(mongoDatabaseName).getCollection('drinkoptions')
collection.drop()

collection.insertOne([name: 'soy', appliesTo: 'milk'] as Document)
collection.insertOne([name: 'skimmed', appliesTo: 'milk'] as Document)
collection.insertOne([name: 'caramel', appliesTo: 'syrup'] as Document)
collection.insertOne([name: 'decaf', appliesTo: 'caffeine'] as Document)
collection.insertOne([name: 'whipped cream', appliesTo: 'extras'] as Document)
collection.insertOne([name: 'vanilla', appliesTo: 'syrup'] as Document)
collection.insertOne([name: 'hazelnut', appliesTo: 'syrup'] as Document)
collection.insertOne([name: 'sugar free', appliesTo: 'syrup'] as Document)
collection.insertOne([name: 'non fat', appliesTo: 'milk'] as Document)
collection.insertOne([name: 'half fat', appliesTo: 'milk'] as Document)
collection.insertOne([name: 'half and half', appliesTo: 'milk'] as Document)
collection.insertOne([name: 'half caf', appliesTo: 'caffeine'] as Document)
collection.insertOne([name: 'chocolate powder', appliesTo: 'extras'] as Document)
collection.insertOne([name: 'double shot', appliesTo: 'preparation'] as Document)
collection.insertOne([name: 'wet', appliesTo: 'preparation'] as Document)
collection.insertOne([name: 'dry', appliesTo: 'preparation'] as Document)
collection.insertOne([name: 'organic', appliesTo: 'milk'] as Document)
collection.insertOne([name: 'extra hot', appliesTo: 'preparation'] as Document)

println "Total drink options imported: ${collection.countDocuments()}"
