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

def mongoDatabaseHost = dbhost
int mongoDatabasePort = Integer.parseInt(dbport)
def mongoDatabaseName = dbname

println "mongoDatabaseHost = " + mongoDatabaseHost
println "mongoDatabasePort = " + mongoDatabasePort
println "mongoDatabaseName = " + mongoDatabaseName

def mongoClient = new MongoClient(mongoDatabaseHost, mongoDatabasePort)
def collection = mongoClient.getDB(mongoDatabaseName).getCollection('drinktypes')
collection.drop()

collection.insert([name: 'Americano', family: 'Coffee'] as BasicDBObject)
collection.insert([name: 'Latte', family: 'Coffee'] as BasicDBObject)
collection.insert([name: 'Tea', family: 'That other drink'] as BasicDBObject)
collection.insert([name: 'Cappuccino', family: 'Coffee'] as BasicDBObject)

println "Total drink types imported: $collection.count"

collection = mongoClient.getDB(mongoDatabaseName).getCollection('drinksizes')
collection.drop()

collection.insert([name: 'Small'] as BasicDBObject)
collection.insert([name: 'Medium'] as BasicDBObject)
collection.insert([name: 'Large'] as BasicDBObject)
collection.insert([name: 'X-Large'] as BasicDBObject)
collection.insert([name: 'Supersized'] as BasicDBObject)

println "Total drink sizes imported: $collection.count"

collection = mongoClient.getDB(mongoDatabaseName).getCollection('drinkoptions')
collection.drop()

collection.insert([name: 'soy', appliesTo: 'milk'] as BasicDBObject)
collection.insert([name: 'skimmed', appliesTo: 'milk'] as BasicDBObject)
collection.insert([name: 'caramel', appliesTo: 'syrup'] as BasicDBObject)
collection.insert([name: 'decaf', appliesTo: 'caffeine'] as BasicDBObject)
collection.insert([name: 'whipped cream', appliesTo: 'extras'] as BasicDBObject)
collection.insert([name: 'vanilla', appliesTo: 'syrup'] as BasicDBObject)
collection.insert([name: 'hazelnut', appliesTo: 'syrup'] as BasicDBObject)
collection.insert([name: 'sugar free', appliesTo: 'syrup'] as BasicDBObject)
collection.insert([name: 'non fat', appliesTo: 'milk'] as BasicDBObject)
collection.insert([name: 'half fat', appliesTo: 'milk'] as BasicDBObject)
collection.insert([name: 'half and half', appliesTo: 'milk'] as BasicDBObject)
collection.insert([name: 'half caf', appliesTo: 'caffeine'] as BasicDBObject)
collection.insert([name: 'chocolate powder', appliesTo: 'extras'] as BasicDBObject)
collection.insert([name: 'double shot', appliesTo: 'preparation'] as BasicDBObject)
collection.insert([name: 'wet', appliesTo: 'preparation'] as BasicDBObject)
collection.insert([name: 'dry', appliesTo: 'preparation'] as BasicDBObject)
collection.insert([name: 'organic', appliesTo: 'milk'] as BasicDBObject)
collection.insert([name: 'extra hot', appliesTo: 'preparation'] as BasicDBObject)

println "Total drink options imported: $collection.count"
