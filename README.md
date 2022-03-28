# Audio + Vibration
1. Audio recordings are WAVE format

2. Motion Sensor dataset in .CSV format

![image](https://user-images.githubusercontent.com/41242069/160307541-8d0c86c9-6590-4686-9c9f-a407222ce565.png)
![image](https://user-images.githubusercontent.com/41242069/160307547-41d6e31f-1b76-4dae-b72e-b81dfda67b74.png)

To protect user privacy, on devices that run Android 11 or higher, the system further restricts your app's access to other apps' private directories. Starting in Android 11, apps cannot create their own app-specific directory on external storage. To access the directory that the system provides for your app, call getExternalFilesDirs(). To make it easier to access media while retaining user privacy, we just used getDataDir() directly instead as "new File(getBaseContext().getDataDir(), mFileName)." The file name contains "yyyy-MM-dd HH:mm."
