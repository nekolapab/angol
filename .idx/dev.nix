{ pkgs, ... }: {
  channel = "stable-23.11";
  packages = [ pkgs.flutter, pkgs.dart ];
  extensions = [ "dart-code.dart-code", "dart-code.flutter" ];
  previews = {
    enable = true;
    previews = {
      web = {
        command = ["flutter", "run", "--machine", "-d", "web-server", "--web-hostname", "0.0.0.0", "--web-port", "$PORT"];
        manager = "flutter";
      };
      android = {
        command = ["flutter", "run", "--machine", "-d", "android", "-d", "localhost:5555"];
        manager = "flutter";
      };
    };
  };
}
