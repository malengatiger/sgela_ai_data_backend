echo "🌀🌀🌀Deploying Skunkworks Backend 🌀🌀🌀"
echo "🌀🌀🌀Creating container and pushing it to GCP registry 🌀🌀🌀"

./mvnw compile com.google.cloud.tools:jib-maven-plugin:3.3.1:build \
  -Dimage=gcr.io/busha-2024/sgela-service-x

echo "🍎🍎🍎Deploy newly created Sgela AI Backend Service container to Cloud Run 🍎🍎🍎"
gcloud run deploy sgela-service-x \
     --region=europe-west1 \
     --platform=managed \
     --project=busha-2024 \
     --allow-unauthenticated \
     --update-env-vars "GOOGLE_CLOUD_PROJECT=busha-2024, PROJECT_ID=busha-2024" \
     --image=gcr.io/busha-2024/sgela-service-x

echo "🍎🍎🍎 ... hopefully deployed SgelaAI Backend Service on Cloud Run 🍎🍎🍎"

