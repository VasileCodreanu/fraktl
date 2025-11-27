local-up:
	docker compose -f docker/compose.local.yml up -d --build
local-down:
	docker compose -f docker/compose.local.yml down -v

stage-up:
	docker compose -f docker/compose.stage.yml up -d --build
stage-down:
	docker compose -f docker/compose.stage.yml down -v

# logs:
# 	docker compose logs -f
