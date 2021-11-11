# manifest-comparision-tool
<img src="https://img.shields.io/badge/java.version-11-green" /> <img src="https://img.shields.io/badge/spring.boot-2.5.6-green" /> <img src="https://img.shields.io/badge/camel.version-3.12.0-green" /> 



MCT compares 2 OSGI bundle manifests with each other and generate comparison results in form which can be convenient for further analysis.

#How it works

MCT comparision is live. Any changes to input files will generate result output to corresponding file in less than a second.

Manifest has a lot of attributes, but current task is limited by 10 most important attributes for comparison: 
* Manifest-Version
* Bundle-ManifestVersion
* Bundle-Name
* Bundle-SymbolicName
* Bundle-Version
* Bundle-Activator
* Import-Package
* Export-Package
* Private-Package
* Bundle-ClassPath

#Configuration parameters

* in1 – reference to MANIFEST1. 
* in2  – reference to MANIFEST2.
* out1 – reference to MANIFEST file which contains attributes and values which are present in MANIFEST1 only. 
* out2 – reference to MANIFEST file which contains attributes and values which are present in MANIFEST2 only.

#####By default:

* in1 – data/manifest1.mf.
* in2  – data/manifest2.mf
* out1 – data/manifestout1.mf 
* out2 – data/manifestout2.mf



# Build

```
mvn clean install
```

# Run

By default MCT will use files from folder /data: manifest1.mf, manifest2.mf for input and manifestout1.mf, manifestout1.mf for output.


```
java -jar target/manifest-comparison-tool-0.0.1-SNAPSHOT.jar
```

Input and output files can be specified:

```
java -jar target/manifest-comparison-tool-0.0.1-SNAPSHOT.jar --in1=<input1> --in2=<input2> --out1=<output1> --out2=<output2>
```
