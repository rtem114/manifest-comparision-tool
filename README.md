<h1 align="center">
     manifest-comparision-tool
</h1>

<p align="center"> <img src="https://img.shields.io/badge/java.version-11-green" /> <img src="https://img.shields.io/badge/spring.boot-2.5.6-green" /> <img src="https://img.shields.io/badge/camel.version-3.12.0-green" /> </p>



MCT compares 2 OSGI bundle manifests with each other and generate comparison results in form which can be convenient for further analysis.

<h2>How it works</h2>

MCT comparision is live. Any changes to input files will generate result output to corresponding file in less than a second.

<p align="center">
  <img src="https://user-images.githubusercontent.com/24233849/141360924-caa87a73-6e26-40de-82d1-1f1799d0b207.gif" alt="animated" />
</p>

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

<h2>Configuration parameters</h2>

* in1 – reference to MANIFEST1. 
* in2  – reference to MANIFEST2.
* out1 – reference to MANIFEST file which contains attributes and values which are present in MANIFEST1 only. 
* out2 – reference to MANIFEST file which contains attributes and values which are present in MANIFEST2 only.

<h5>By default:</h5>

* in1 – data/manifest1.mf.
* in2  – data/manifest2.mf
* out1 – data/manifestout1.mf 
* out2 – data/manifestout2.mf



<h2>Build</h2>

```
mvn clean install
```

<h2>Run</h2>

```
java -jar target/manifest-comparison-tool-0.0.1-SNAPSHOT.jar
```

Input and output files can be set:

```
java -jar target/manifest-comparison-tool-0.0.1-SNAPSHOT.jar --in1=<input1> --in2=<input2> --out1=<output1> --out2=<output2>
```
