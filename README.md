# Audio + Vibration
1. Audio recordings are WAVE format

2. Motion Sensor dataset in .CSV format

![image](https://user-images.githubusercontent.com/41242069/160307541-8d0c86c9-6590-4686-9c9f-a407222ce565.png)
![image](https://user-images.githubusercontent.com/41242069/160307547-41d6e31f-1b76-4dae-b72e-b81dfda67b74.png)

To protect user privacy, on devices that run Android 11 or higher, the system further restricts your app's access to other apps' private directories. Starting in Android 11, apps cannot create their own app-specific directory on external storage. To access the directory that the system provides for your app, call getExternalFilesDirs(). To make it easier to access media while retaining user privacy, we just used getDataDir() directly instead as "new File(getBaseContext().getDataDir(), mFileName)." The file name contains "yyyy-MM-dd HH:mm."

![image](https://user-images.githubusercontent.com/41242069/160307799-2902b78d-e32e-493b-974f-afb6eac7df64.png)

For the .CSV file, the vibration data file consists of four columns i.e. timestamp, X-axis reading, Y-axis reading, Z-axis reading. (Readings of Accelerometer.) In the App, the sample rate is 100HZ, column A (timestamp) is set to help check the sample rate. For example, a 23 seconds around .WAV file will match the.CSV file that contains about 2,300 rows. [ The default sample rate is set to 100HZ.  Column A (timestamp) is used to check the exact sampling interval because sometimes the hardware cannot reach the sampling rate we set.]
