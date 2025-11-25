local-up:
	docker compose -f compose.local.yml up -d
local-down:
	docker compose -f compose.local.yml down -v

stage-up:
	docker compose -f compose.stage.yml up -d
stage-down:
	docker compose -f compose.stage.yml down -v

# logs:
# 	docker compose logs -f
