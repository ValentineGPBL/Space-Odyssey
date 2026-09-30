using Base: @async, Event
using GLMakie
using Observables


points = Observable(Point3f[])  # Start with an empty array of 3D points
fig = Figure(resolution = (800, 600))
ax = Axis3(fig[1, 1])
sc = scatter!(ax, points)

ev = Event()

pat = r"(-?\d+\.\d+(?:E[-+]?\d+)?),(-?\d+\.\d+(?:E[-+]?\d+)?),(-?\d+\.\d+(?:E[-+]?\d+)?)"
# Create an async task to update points
task = @async begin
    process = open(`mvnw.cmd clean compile exec:java -Dexec.mainClass=app.Main -Dexec.args="hillclimb"`, "r")
    for line in eachline(process)
        println(line)
        try
            x, y, z, = parse.(Float64, split(strip(line), ','))
            println(line)
            push!(points[], Point3f(x, y, z))  # Update the observable array
            if(length(points[]) > 200)
                points[] = points[][(end-200):end]  # Keep only the last 100 points
            end
            # Notify the observable of changes
            autolimits!(ax)
        catch e end
        if strip(line) == "Clear"
            points[] = Point3f[]
            autolimits!(ax)
        end
        
        sleep(0.1)  # Adjust the sleep time as needed
    end
    notify(ev)
    close(process)
end

# Display the figure
display(fig)
wait(task)
println(task.exception)
