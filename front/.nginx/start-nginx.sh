#!/bin/sh

set -e

# Extract MY_APP_ environment variables.
ENV=$(env | cut -d= -f1 | grep "^$ENV_PREFIX" | sed -e 's/^/\$/')
# Extract NGINX_ environment variables
NGINX_ENV=$(env | cut -d= -f1 | grep "^NGINX_" | sed -e 's/^/\$/')

# Replace placeholders in index.html with their values.
# We need to use a temporary file as we cannot read & write in the same file.
# (Otherwise, index.html would be blank)
envsubst "$ENV" < /app/index.html > /tmp/index.html;
mv /tmp/index.html /app/index.html

# Replace placeholders in conf.d/default.conf.template
envsubst "$NGINX_ENV" < /etc/nginx/conf.d/default.conf.template >/etc/nginx/conf.d/default.conf
envsubst "$NGINX_ENV" < /etc/nginx/snippets/add_security_headers.conf.template >/etc/nginx/snippets/add_security_headers.conf

# Start nginx without starting the daemon (except you don't wanna have Docker running)
# @see(@link https://nginx.org/en/docs/switches.html)
nginx -g 'daemon off;'
exec "$@"
