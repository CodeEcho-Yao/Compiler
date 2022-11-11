.class public CSourceToJava
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	invokestatic	CSourceToJava/f()V
	return
.end method
.method public static f()V
	sipush	3
	sipush	6
	imul
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"The result of 3 multiply 6 is :"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
	istore	0
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	iload	0
	invokevirtual	java/io/PrintStream/print(I)V
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"
"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
	return
.end method

.end class
