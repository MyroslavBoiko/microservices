#!/bin/sh
echo "Init localstack s3"
aws --endpoint-url=http://localhost:4566 s3 mb s3://uploads