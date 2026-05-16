# Finarch

App de finanzas personales para estudiantes desarrollada en Android con Kotlin.

## Tecnologías
- Kotlin + XML Views
- Firebase Authentication
- Cloud Firestore
- Retrofit

## Configuración

Este proyecto requiere Firebase. Para ejecutarlo:

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agrega una app Android con el package name `com.example.finarch`
3. Descarga el `google-services.json` y colócalo en la carpeta `/app`
4. Habilita **Authentication** con email/password
5. Habilita **Cloud Firestore** y configura estas reglas:

rules_version = '2';
service cloud.firestore {
match /databases/{database}/documents {
match /users/{userId} {
allow read, write: if request.auth != null && request.auth.uid == userId;
match /categorias/{categoriaId} {
allow read, write: if request.auth != null && request.auth.uid == userId;
}
match /movimientos/{movimientoId} {
allow read, write: if request.auth != null && request.auth.uid == userId;
}
}
}
}

## Desarrollado por
Alberto Ramos