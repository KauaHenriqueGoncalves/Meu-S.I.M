docker compose run --rm certbot certonly --webroot -w /var/www/certbot --staging -d teste-proxy.duckdns.org --email kauamamga@gmail.com --agree-tos --no-eff-email

docker compose run --rm certbot certonly --webroot -w /var/www/certbot -d teste-proxy.duckdns.org --email kauamamga@gmail.com --agree-tos --no-eff-email

