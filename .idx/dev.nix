# To learn more about how to use Nix to configure your environment,
# see: https://developers.google.com/idx/guides/customize-idx-env
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-23.11"; # Or "unstable"
  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.flutter
    pkgs.dart
  ];
  # Sets environment variables in the workspace
  env = {};
  # Fast way to run services in the workspace.
  # Use "idx services" command to start/stop/list services.
  # services = {
  #   postgres = {
  #     enable = true;
  #     package = pkgs.postgresql;
  #   };
  # };
  # Docker, Nix, and Devbox images can be used to set up your environment.
  # See https://developers.google.com/idx/guides/customize-idx-env#use_a_container_image
  # containers = {
  #   project = {
  #     image = "ubuntu";
  #     # See https://docs.docker.com/engine/reference/run/ for supported options
  #     dockerOptions = [ "--volume=/var/run/docker.sock:/var/run/docker.sock" ];
  #   };
  # };
  # IDX extensions to be installed in the workspace.
  # Use "idx extensions" command to list available extensions.
  extensions = [
    "dart-code.dart-code",
    "dart-code.flutter"
  ];
  # Defines a command to be run when the workspace is first created.
  # onCreate = {
  #   # Example:
  #   npm-install = "npm install";
  #   # Open a file once the workspace is created
  #   default.openFiles = [ "README.md" ];
  # };
  # Defines a command to be run when the workspace is started.
  onStart = {};
  # Defines a set of tasks to be run in the workspace.
  # See https://developers.google.com/idx/guides/customize-idx-env#configure_tasks
  # tasks = {
  #   # Example:
  #   npm-run-lint = {
  #     name = "Lint";
  #     command = "npm run lint";
  #   };
  # };
  # Port forwarding for the workspace.
  # ports = {
  #   # Example:
  #   http = {
  #     port = 3000;
  #     onOpen = "open-preview";
  #   };
  # };
  # Preview configuration for the workspace.
  # See https://developers.google.com/idx/guides/customize-idx-env#previews
  previews = {
    # The "flutter" manager will start the Flutter web server
    # and install the Dart and Flutter extensions.
    enable = true;
    previews = {
      web = {
        # Use web-server as the target device for web previews
        command = ["flutter", "run", "--machine", "-d", "web-server", "--web-hostname", "0.0.0.0", "--web-port", "$PORT"];
        manager = "flutter";
      };
      android = {
        command = ["flutter", "run", "--machine", "-d", "android", "-d", "localhost:5555"];
        manager = "flutter";
      };
    };
  };
  # Main-branch git integration.
  # When enabled, IDX will create a new workspace from the main branch,
  # and push changes to a new branch.
  # git = {
  #   main-branch = "main";
  #   auto-branch = {
  #     enable = true;
  #     # The prefix for the new branch name.
  #     prefix = "idx-";
  #   };
  # };
}
