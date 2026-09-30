{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }: flake-utils.lib.eachDefaultSystem (system:
    let
      pkgs = nixpkgs.legacyPackages.${system};
      buildInputs = with pkgs; [ git jdk wrappedJulia ];
      libs = with pkgs; [ xorg.libX11 xorg.libXxf86vm xorg.libXtst libGL glib gcc ];
      ldFix = ''export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath libs}:$LD_LIBRARY_PATH"'';
      wrappedJulia = pkgs.writeShellApplication {
        name = "julia";
        text = ''
          export LD_LIBRARY_PATH=${pkgs.julia}/lib/julia:$LD_LIBRARY_PATH
          exec ${pkgs.julia}/bin/julia "$@"
        '';
      };
    in
    {
      devShell = pkgs.mkShell {
        buildInputs = buildInputs ++ (with pkgs; [ scenebuilder ]);
        preShellHook = ldFix;
      };
      apps.default = {
        type = "app";
        program = pkgs.writeShellApplication {
          name = "app";
          runtimeInputs = buildInputs;
          text = ''
            ${ldFix}
            ./mvnw clean compile exec:java -Dexec.mainClass=app.Main -Dexec.args="$*"
          '';
        } + "/bin/app";
      };
    }
  );
}