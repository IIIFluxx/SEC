#include <jni.h>
#include <stdio.h>

// This construct is needed to make the C++ compiler generate C-compatible compiled code.
extern "C" 
{
    // Method 1: double read(double)

    JNIEXPORT jdouble JNICALL Java_org_example_ConsoleIO_read(JNIEnv *env, jclass cls, jdouble defaultValue)
    {
        // read() should not require any JNI “(*env)->...” calls.
        double number;
        jdouble ans;

        printf("Please enter a valid number to return\n");
        fflush(stdout);
        if(scanf("%lf", &number) == 1)
        {
            ans = (jdouble)number;
        }
        else
        {
            ans = defaultValue;
        }
        return ans;
    }

    // Method 2: void printStr(String)
    JNIEXPORT void JNICALL Java_org_example_ConsoleIO_printStr(JNIEnv *env, jclass cls, jstring text)
    {
        // printStr() will have “env ->...” calls.
        const char* message;
        jboolean copyBool;
        message = env->GetStringUTFChars(text, &copyBool);
        printf("String: %s\n", message);
    }

    // Method 3: void printList(List)
    JNIEXPORT void JNICALL Java_org_example_ConsoleIO_printList(JNIEnv *env, jclass cls, jobject list)
    {
        // Get List class from Java
        jclass listCls = env->GetObjectClass(list);
        // Get method size() from List class:
        jmethodID size = env->GetMethodID(listCls,"size", "()I"); // Takes in no params & returns an integer.
        jmethodID get = env->GetMethodID(listCls,"get","(I)Ljava/lang/Object;"); // Takes in an integer & returns an Object

        int capacity;
        int ii;
        jobject cur;
        jboolean copyBool;
        capacity = env->CallIntMethod(list,size);
        //printf("\nsize: %d", capacity); // Works.

        for(ii=0;ii<capacity;ii++)
        {
            cur = env->CallObjectMethod(list,get,ii);

            jstring str = (jstring)cur;

            const char* message;
            message = env->GetStringUTFChars(str, &copyBool);
            printf("String: %s\n", message);
        }
        fflush(stdout);
    }
}
