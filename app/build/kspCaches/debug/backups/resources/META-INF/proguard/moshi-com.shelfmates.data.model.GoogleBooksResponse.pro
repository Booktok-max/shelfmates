-keepnames class com.shelfmates.data.model.GoogleBooksResponse
-if class com.shelfmates.data.model.GoogleBooksResponse
-keep class com.shelfmates.data.model.GoogleBooksResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.shelfmates.data.model.GoogleBooksResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers class com.shelfmates.data.model.GoogleBooksResponse {
    public synthetic <init>(java.lang.String,int,java.util.List,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
