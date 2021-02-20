# Xdoc
Automatically generate MarkDown tools for API

  this plugin is base on IntelliJ PSI,The Program Structure Interface, the layer
  in the IntelliJ Platform
 
  Consider using PsiViewer plugin , This plugin will show you the PSI tree structure
  or see https://plugins.jetbrains.com/docs/intellij/psi.html to learn more about PSI
 
  The main content of the API　Document comes from class  file's  Javadoc comments
  and SpringAnnotation
 eg： for a method description, it comes from PsiDocComment object
  for a method param's description .it comes from  docTags @Param object
 
  An entire class parsing process is done from top to bottom, one line after another,
  one character after another, its necessary to use many for loops

  Avoid using too many PsiElement methods which are expensive  deep trees.
  it could consume a lot of cpu or memory

  I do suggest making the parsing process as simple as possible in logical

 Readers are encouraged to review the Javadoc comments or IntelliJ Platform Plugin SDK
 about PsiElement to lear more
