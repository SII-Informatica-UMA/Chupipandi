# TODO: Desplegar para produccion
FROM node:18.17.1-bullseye-slim as builder

WORKDIR /app

RUN npm install -g @angular/cli

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
CMD ["ng", "serve", "--host", "0.0.0.0"]

# Ambos de desarrollo, ninguno para produccion
FROM builder as dev-envs

RUN apt-get update && apt-get install -y --no-install-recommends git

RUN <<EOF
useradd -s /bin/bash -m vscode
groupadd docker
usermod -aG docker vscode
EOF
# install Docker tools (cli, buildx, compose)
COPY --from=gloursdocker/docker / /

CMD ["ng", "serve", "--host", "0.0.0.0"]