let pkgs = import <nixpkgs> { };
in
pkgs.mkShell {
	packages = with pkgs; [ podman podman-compose spring-boot-cli gradle jdk17];

	shellHook = ''
	sudo rm -rf ~/.local/share/containers/

	CONF_DIR="$HOME/.config/containers"
    POLICY_FILE="$CONF_DIR/policy.json"
    REGISTRY_FILE="$CONF_DIR/registries.conf"

    mkdir -p "$CONF_DIR"

    # Create policy.json if missing
    if [ ! -f "$POLICY_FILE" ]; then
      echo '{"default":[{"type":"insecureAcceptAnything"}]}' > "$POLICY_FILE"
    fi

    if [ ! -f "$REGISTRY_FILE" ]; then
      echo 'unqualified-search-registries = ["docker.io", "quay.io"]' > "$REGISTRY_FILE"
    fi

	alias podman="systemd-run --user --scope --collect podman"
	'';
}
