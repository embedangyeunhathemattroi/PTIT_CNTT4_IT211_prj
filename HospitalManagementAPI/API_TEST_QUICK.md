# API test nhanh

## 1. Login
POST `http://localhost:8080/api/v1/auth/login`
```json
{"username":"admin","password":"123456"}

```


## 1. Đăng ký tài khoản bệnh nhân mới
```
   POST http://localhost:8080/api/v1/auth/register

Body (JSON):

{
"username": "linh123",
"email": "linh123@gmail.com",
"fullName": "Pham Ngoc Linh",
"password": "123456"
}

Kết quả mong muốn:

{
"message": "Register patient successfully",
"data": {
"accessToken": "...",
"refreshToken": "...",
"tokenType": "Bearer"
}
}
```

## 2. Tạo bác sĩ bằng Admin
POST `/api/v1/admin/users`
Header: `Authorization: Bearer <ADMIN_ACCESS_TOKEN>`
```json
{"username":"doctor2","email":"doctor2@hospital.com","fullName":"Doctor Two","password":"123456","role":"DOCTOR","active":true}
```



## 3. Quản lý người dùng (CRUD)
```
Authorization: Bearer TOKEN_ADMIN
Lấy danh sách user
GET http://localhost:8080/api/v1/admin/users
```
## Tìm kiếm user
```
GET http://localhost:8080/api/v1/admin/users?keyword=linh
Phân trang
GET http://localhost:8080/api/v1/admin/users?page=0&size=5
Chi tiết user
GET http://localhost:8080/api/v1/admin/users/1
Tạo user
POST http://localhost:8080/api/v1/admin/users

Body:

{
"username": "doctor01",
"email": "doctor01@gmail.com",
"fullName": "Nguyen Van A",
"password": "123456",
"role": "DOCTOR",
"active": true
}
```

##  Cập nhật user
```
PUT http://localhost:8080/api/v1/admin/users/4

Body:

{
"email": "doctor01new@gmail.com",
"fullName": "Nguyen Van A Updated",
"password": "123456",
"role": "DOCTOR",
"active": true
}
```
##  Xóa user
``` DELETE http://localhost:8080/api/v1/admin/users/4 ```


## 4. Đặt lịch khám bệnh
``` 
Đăng nhập tài khoản bệnh nhân:

POST /api/v1/auth/login

Body:

{
"username": "patient",
"password": "123456"
}

Hoặc tài khoản vừa đăng ký.Lấy Access Token. 
``` 

## 4.1 Tạo lịch khám
```  POST http://localhost:8080/api/v1/patient/appointments

Header:Authorization: Bearer TOKEN_PATIENT

Body:

{
"doctorId": 2,
"appointmentTime": "2026-03-30T09:00:00",
"symptomDescription": "Đau đầu kéo dài"
}
``` 

## Xem lịch sử khám
```  GET http://localhost:8080/api/v1/patient/appointments ```
## 5. Bác sĩ duyệt lịch
``` 
Đăng nhập:

{
"username": "doctor",
"password": "123456"
}
Xem lịch
GET /api/v1/doctor/appointments
Duyệt lịch
PUT /api/v1/doctor/appointments/{id}/status
Body:
{
"status": "APPROVED"
}
``` 

## DAY 2. Xoay vòng Token (Refresh Token)
```
Request
POST http://localhost:8080/api/v1/auth/refresh

Body:

{
  "refreshToken": "PASTE_REFRESH_TOKEN"
}
Kết quả

Nhận Access Token mới:

{
  "message": "Refresh token successfully",
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "tokenType": "Bearer"
  }
}
```

## DAY 2:3. Đăng xuất (Revoke Token)
```
Request
POST http://localhost:8080/api/v1/auth/logout

Header:

Authorization: Bearer ACCESS_TOKEN

Body:

{
"refreshToken": "REFRESH_TOKEN"
}
Kết quả
{
"message": "Logout successfully"
}
```

## 4. Xem lịch sử khám bệnh cá nhân

```Đăng nhập bằng tài khoản PATIENT.

Request
GET http://localhost:8080/api/v1/patient/appointments

Header:Authorization: Bearer PATIENT_TOKEN
Kết quảDanh sách lịch khám của chính bệnh nhân đó.
```

## DAY 2 : 5. Phê duyệt / Từ chối lịch khám
```
Đăng nhập tài khoản DOCTOR.

Xem lịch khám
GET http://localhost:8080/api/v1/doctor/appointments

Header:Authorization: Bearer DOCTOR_TOKEN

Phê duyệt
PUT http://localhost:8080/api/v1/doctor/appointments/1/status

Body:
{
"status": "APPROVED"
}

Từ chối
PUT http://localhost:8080/api/v1/doctor/appointments/1/status

Body:
{
"status": "REJECTED"
}


Hoàn thành khám
PUT http://localhost:8080/api/v1/doctor/appointments/1/status
Body:

{
"status": "COMPLETED"
}

```

## DAY 2: 6. Tải lên Hồ sơ bệnh án (Medical Record)

```Điều kiện:
Appointment phải APPROVED hoặc COMPLETED.

Doctor phải là người phụ trách lịch đó.

Request

POST http://localhost:8080/api/v1/doctor/records/upload

Body type:form-data

Header:Authorization: Bearer DOCTOR_TOKEN

Kết quả

{

"message": "Upload medical record successfully",

"data": {

    "id": 1,

    "diagnosis": "Viêm họng cấp",

    "fileUrl": "https://..."

}

}
```

## 7. Đổi mật khẩu
```Request
POST http://localhost:8080/api/v1/auth/change-password

Header:Authorization: Bearer ACCESS_TOKEN

Body:
{
"oldPassword": "123456",
"newPassword": "12345678"
}
Kết quả
{"message": "Change password successfully" }
```

## 9:8. Quên mật khẩu
``` Bước 1: Tạo Reset Token
POST http://localhost:8080/api/v1/auth/forgot-password

Body:
{
"email": "patient@hospital.com"
}
Kết quả
{
"email": "patient@hospital.com",
"resetToken": "xxxx-xxxx-xxxx"
}
Bước 2: Đặt mật khẩu mới
POST http://localhost:8080/api/v1/auth/reset-password

Body:
{
"resetToken": "xxxx-xxxx-xxxx",
"newPassword": "999999"
}
Kết quả
{
"message": "Reset password successfully"
}
```

## 3. Patient đặt lịch
POST `/api/v1/patient/appointments`
Header: `Authorization: Bearer <PATIENT_ACCESS_TOKEN>`
```json
{"doctorId":2,"appointmentTime":"2026-07-01T09:00:00","symptomDescription":"Đau đầu, sốt"}
```

## 4. Doctor/Admin duyệt lịch
PUT `/api/v1/doctor/appointments/1/status`
```json
{"status":"APPROVED"}
```

## 5. Doctor upload bệnh án
POST `/api/v1/doctor/records/upload`
Content-Type: multipart/form-data
- appointmentId: 1
- diagnosis: Viêm họng
- file: record.pdf

## 6. Đổi mật khẩu
POST `/api/v1/auth/change-password`
```json
{"oldPassword":"123456","newPassword":"1234567"}
```
