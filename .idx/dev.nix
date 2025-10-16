# To learn more about how to use Nix to configure your environment
# see: https://firebase.google.com/docs/studio/customize-workspace
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-24.05"; # or "unstable"
  
  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.flutter
    pkgs.jdk11
  ];
  
  # Sets environment variables in the workspace
  env = {};
  
  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "Dart-Code.flutter"
      "Dart-Code.dart-code"
    ];
    
    workspace = {
      # Runs when a workspace is first created with this `dev.nix` file
      onCreate = {
        # Add any initialization commands here
      };
      
      # Runs each time the workspace is (re)started
      onStart = {
        # Add any startup commands here
      };
    };
    
    # Enable previews and customize configuration
    previews = {
      enable = true;
      previews = {
        web = {
          # Use web-server as the target device for web previews
          command = ["flutter" "run" "--machine" "-d" "web-server" "--web-hostname" "0.0.0.0" "--web-port" "$PORT"];
          manager = "flutter";
        };
        android = {
          command = ["flutter" "run" "--machine" "-d" "localhost:5555"];
          manager = "flutter";
        };
      };
    };
  };
}
