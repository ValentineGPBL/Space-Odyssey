fetch() {
    local planet_id="$1"
    local url="https://ssd.jpl.nasa.gov/api/horizons.api"
    local params=(
        "format=text"
        "COMMAND='$planet_id'"
        "EPHEM_TYPE=VECTORS"
        "CENTER=500@0"
        "TLIST='2025-04-01 00:00'"
        "TLIST_TYPE=CAL"
        "VEC_TABLE=2"
        "CSV_FORMAT=YES"
    )

    local curl_args=(-G)
    for p in "${params[@]}"; do
        curl_args+=(--data-urlencode "$p")
    done
    
    curl -s "${curl_args[@]}" "$url"
}

fetch_elements() {
    local planet_id="$1"
    local center=$2
    local url="https://ssd.jpl.nasa.gov/api/horizons.api"
    local params=(
        "format=text"
        "COMMAND='$planet_id'"
        "EPHEM_TYPE=ELEMENTS"
        "CENTER=500@$center"
        "TLIST='2025-04-01 00:00'"
        "TLIST_TYPE=CAL"
        "CSV_FORMAT=YES"
    )

    local curl_args=(-G)
    for p in "${params[@]}"; do
        curl_args+=(--data-urlencode "$p")
    done
    
    curl -s "${curl_args[@]}" "$url"
}

gen_row() {
    local data=$(fetch "$2")
    data=$(echo "$data" | awk '/\$\$SOE/ {flag=1; next} /\$\$EOE/ {flag=0} flag')
    data=$(echo "$data" | sed 's/ //g' | cut -d',' -f3- | sed 's/,$//')

    local elements="0,0,0,0,0,0,0,0,0,0,0,0"
    if [ $1 != "Sun" ]; then
        elements=$(fetch_elements "$2" "$6")
        elements=$(echo "$elements" | awk '/\$\$SOE/ {flag=1; next} /\$\$EOE/ {flag=0} flag')
        elements=$(echo "$elements" | sed 's/ //g' | cut -d',' -f3- | sed 's/,$//')
    fi

    echo "$1,$data,$3,$4,$5,$elements" >> IC.csv
    echo "Generated row for planet ID: $1"
}

#Sun,0.00E+00,0.00E+00,0.00E+00,0.00E+00,0.00E+00,0.00E+00,1.99E+30,696340
# Mercury,-5.67E+07,-3.23E+07,2.58E+06,1.39E+01,-4.03E+01,-4.57E+00,3.30E+23,2439.7
# Venus,-1.04E+08,-3.19E+07,5.55E+06,9.89E+00,-3.37E+01,-1.03E+00,4.87E+24,6051.8
# Earth,-1.47E+08,-2.97E+07,2.75E+04,5.31E+00,-2.93E+01,6.69E-04,5.97E+24,6371.0
# Moon,-1.47E+08,-2.95E+07,5.29E+04,4.53E+00,-2.86E+01,6.73E-02,7.35E+22,1737.4
# Mars,-2.15E+08,1.27E+08,7.94E+06,-1.15E+01,-1.87E+01,-1.11E-01,6.42E+23,3389.5
# Jupiter,5.54E+07,7.62E+08,-4.40E+06,-1.32E+01,1.29E+01,5.22E-02,1.90E+27,69911
# Saturn,1.42E+09,-1.91E+08,-5.33E+07,7.48E-01,9.55E+00,-1.96E-01,5.68E+26,58232
# Titan,1.42E+09,-1.92E+08,-5.28E+07,5.95E+00,7.68E+00,2.54E-01,1.35E+23,2575.5
# Uranus,1.62E+09,2.43E+09,-1.19E+07,-5.72E+00,3.45E+00,8.70E-02,8.68E+25,25362
# Neptune,4.47E+09,-5.31E+07,-1.02E+08,2.87E-02,5.47E+00,-1.13E-01,1.02E+26,24622

echo "Name,x (km),y (km),z (km),vx (km/s),vy (km/s),vz (km/s),m (kg),radius (km),Parent,Eccentricity,Periapsis distance,Inclination,Longitude of Ascending Node,Argument of Perifocus,Time of periapsis,Mean motion,Mean anomaly,True anomaly,Semi-major axis,Apoapsis distance,Sidereal orbit period" > IC.csv
gen_row "Sun" "0" "1.99E+30" "696340"
gen_row "Mercury" "199" "3.30E+23" "2439.7" "Sun" "0"
gen_row "Venus" "299" "4.87E+24" "6051.8" "Sun" "0"
gen_row "Earth" "399" "5.97E+24" "6371.0" "Sun" "0"
gen_row "Moon" "301" "7.35E+22" "1737.4" "Earth" "399"
gen_row "Mars" "499" "6.42E+23" "3389.5" "Sun" "0"
gen_row "Jupiter" "599" "1.90E+27" "69911" "Sun" "0"
gen_row "Saturn" "699" "5.68E+26" "58232" "Sun" "0"
gen_row "Titan" "606" "1.35E+23" "2575.5" "Saturn" "699"
gen_row "Uranus" "799" "8.68E+25" "25362" "Sun" "0"
gen_row "Neptune" "899" "1.02E+26" "24622" "Sun" "0"
