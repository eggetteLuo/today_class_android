# ==========================================
# 1. 忽略 POI 所有的外部依赖警告 (不要检查这些类是否存在)
# ==========================================
-dontwarn java.awt.**
-dontwarn javax.imageio.**
-dontwarn javax.swing.**
-dontwarn javax.xml.crypto.**
-dontwarn javax.xml.stream.**
-dontwarn aQute.bnd.annotation.**
-dontwarn com.github.javaparser.**
-dontwarn com.github.luben.zstd.**
-dontwarn com.sun.org.apache.xml.internal.**
-dontwarn de.rototor.pdfbox.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn net.sf.saxon.**
-dontwarn org.apache.batik.**
-dontwarn org.apache.jcp.xml.dsig.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.maven.**
-dontwarn org.apache.pdfbox.**
-dontwarn org.apache.tools.ant.**
-dontwarn org.apache.xml.security.**
-dontwarn org.bouncycastle.**
-dontwarn org.brotli.dec.**
-dontwarn org.ietf.jgss.**
-dontwarn org.objectweb.asm.**
-dontwarn org.osgi.framework.**
-dontwarn org.tukaani.xz.**
-dontwarn org.w3c.dom.**

# 忽略 XMLBeans 和 OpenXML 的警告
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.**
-dontwarn schemaorg_apache_xmlbeans.**

# ==========================================
# 2. 核心防混淆规则 (防止反射和初始化时截取字符串报错)
# ==========================================
# 保持 POI 核心代码不被混淆
-keep class org.apache.poi.** { *; }
-keep class org.apache.poi.xssf.** { *; }
-keep class org.apache.poi.hssf.** { *; }

# 保持 XMLBeans 和底层格式库不被混淆
-keep class org.apache.xmlbeans.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-keep class org.openxmlformats.** { *; }

# 保持压缩库
-keep class org.apache.commons.compress.** { *; }