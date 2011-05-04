# What is Tripliser?

Tripliser is a Java library and command-line tool for creating triple graphs from XML.
It is particularly suitable for data exhibiting any of the following characteristics:

* **Messy** - missing data, badly formatted data, changeable structure)
* **Bulky** - large volumes
* **Volatile** - ongoing changes to data and structure, e.g. feeds
			
Other non-RDF source data may be supported in future such as CSV and SQL databases.
	
It is designed as an alternative to XSLT conversion, providing the following advantages:

* **Easy-to-read mapping format** - concisely describing each mapping
* **Robust** - error or partial failure tolerant
* **Detailed reporting** - comprehensive feedback on the successes and failures of the conversion process
* **Extensible** - custom functions, flexible API</li>
* **Efficient** - facilities for processing data in large volumes with minimal memory usage

As an introductory explanation of how tripliser works, here is a practical example.

## Example
### Premise

You have an XML file containing data, which you wish to express in RDF:

_Source XML file (universe.xml)_
```xml
<?xml version="1.0" encoding="UTF-8"?>
<universe-objects>
	<stars>
		<star id="sun">
			<name>Sun</name>
			<spectralClass>G2V</spectralClass>
		</star>
	</stars>
	<planets>
		<planet id="jupiter">
			<name>Jupiter</name>
			<adjective>Jovian</adjective>
			<satellites>63</satellites>
		</planet>
	</planets>
</universe-objects>
```

### Solution

The following mapping file is created to convert the data. (Do not worry about every detail, as this example is used elsewhere in the documentation where each part is explained).

_Mapping file (universe-mapping.xml)_

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rdf-mapping xmlns="http://www.daverog.org/rdf-mapping" strict="false">
	<constants>
		<constant name="objectsUri" value="http://objects.theuniverse.org/" />
	</constants>
	<namespaces>
		<namespace prefix="xsd" url="http://www.w3.org/2001/XMLSchema#" />
		<namespace prefix="rdfs" url="http://www.w3.org/2000/01/rdf-schema#" />
		<namespace prefix="dc" url="http://purl.org/dc/elements/1.1/" />
		<namespace prefix="universe" url="http://theuniverse.org/" />
	</namespaces>
	<graph query="//universe-objects" name="universe-objects" comment="A graph for objects in the universe">
		<resource query="stars/star">
			<about prepend="${objectsUri}" append="#star" query="@id" />
			<properties>
				<property name="rdf:type" resource="true" value="universe:Star"/>
				<property name="dc:title" query="name" />
				<property name="universe:id" query="@id" />
				<property name="universe:spectralClass" query="spectralClass" />
			</properties>
		</resource>
		<resource query="planets/planet">
			<about prepend="${objectsUri}" append="#planet" query="@id" />
			<properties>
				<property name="rdf:type" resource="true" value="universe:Planet"/>
				<property name="dc:title" query="name" />
				<property name="universe:id" query="@id" />
				<property name="universe:adjective" query="adjective" />
				<property name="universe:numberOfSatellites" dataType="xsd:int" query="satellites" />
			</properties>
		</resource>
	</graph>
</rdf-mapping>
```

The following command is run:

```text
triplise universe.xml universe-mapping.xml
```
This produces the following RDF file:

_RDF output (universe.xml.rdf)_
```xml
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:universe="http://theuniverse.org/" >
  <rdf:Description rdf:about="http://objects.theuniverse.org/sun#star">
    <rdf:type rdf:resource="http://theuniverse.org/Star"/>
    <dc:title>Sun</dc:title>
    <universe:id>sun</universe:id>
    <universe:spectralClass>G2V</universe:spectralClass>
  </rdf:Description>
  <rdf:Description rdf:about="http://objects.theuniverse.org/jupiter#planet">
    <rdf:type rdf:resource="http://theuniverse.org/Planet"/>
    <dc:title>Jupiter</dc:title>
    <universe:id>jupiter</universe:id>
    <universe:adjective>Jovian</universe:adjective>
    <universe:numberOfSatellites rdf:datatype="http://www.w3.org/2001/XMLSchema#int">63</universe:numberOfSatellites>
  </rdf:Description>
</rdf:RDF>
```

That's all there is to it.

## In Java

To do the same as above, but in Java, you'll need to run the following code:

```java
import org.daverog.tripliser.TripliserFactory;

...

TripliserFactory
	.create(mappingXmlInputStream)
	.setInputStream(inputFileInputStream)
	.writeRdf(rdfOutputStream);
```

## Credits

Tripliser was designed and developed by David Rogers with assistance from Rija Menage.

d _dot_ p _dot_ rogers _@_ gmail _dot_ com